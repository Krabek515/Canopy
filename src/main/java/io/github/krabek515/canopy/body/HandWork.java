package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.block.CanopyBlocks;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.item.MacheteItem;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;

public final class HandWork {
    public static final TagKey<Block> THICKET = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "thicket"));
    public static final TagKey<Block> FOLIAGE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "foliage"));
    public static final TagKey<Block> SOIL = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "soil"));
    public static final TagKey<Block> VEIN = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "vein"));

    private HandWork() {
    }

    public static boolean holdingMachete(Player player) {
        return isMachete(player.getMainHandItem());
    }

    public static boolean isMachete(ItemStack stack) {
        return stack.getItem() instanceof MacheteItem;
    }

    public static boolean holdingMetal(Player player) {
        return player.getMainHandItem().getItem() instanceof MacheteItem machete && machete.metal();
    }

    public static boolean holdingGouge(Player player) {
        return player.getMainHandItem().is(CanopyItems.GOUGE.get());
    }

    public static void slowInThicket(Player player) {
        if (player.isCreative() || player.isSpectator() || holdingMachete(player)) {
            return;
        }
        BlockState feet = player.level().getBlockState(player.blockPosition());
        BlockState head = player.level().getBlockState(player.blockPosition().above());
        BlockState thicket = feet.is(THICKET) ? feet : head.is(THICKET) ? head : null;
        if (thicket == null || thicket.is(CanopyBlocks.THORNY_BRUSH.get())) {
            return;
        }
        player.makeStuckInBlock(thicket, new Vec3(0.35, 0.75, 0.35));
        if (player.level().isClientSide || !(player instanceof ServerPlayer server)) {
            return;
        }
        if (Math.abs(player.getX() - player.xOld) < 0.003 && Math.abs(player.getZ() - player.zOld) < 0.003) {
            return;
        }
        server.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> stats.setStamina(stats.stamina() - 0.03f));
    }

    public static void adjustBreak(PlayerEvent.BreakSpeed event, SurvivalStats stats) {
        Player player = event.getEntity();
        BlockState state = event.getState();
        boolean machete = holdingMachete(player);
        boolean metal = holdingMetal(player);
        boolean gouge = holdingGouge(player);
        Kind kind = kind(state);
        float speed = switch (kind) {
            case VEIN -> gouge ? 6f : 0.04f;
            case FOLIAGE -> machete ? 4f : 2f;
            case THICKET -> metal ? 10f : machete ? 8f : 0.25f;
            case SOIL -> 0.22f;
            case WOOD -> metal ? 2f : machete ? 1.5f : 0.12f;
            case HARD -> 0.04f;
            case OTHER -> event.getNewSpeed();
        };
        boolean strain = kind == Kind.HARD || (kind == Kind.VEIN && !gouge) || ((kind == Kind.WOOD || kind == Kind.THICKET) && !machete);
        if (!player.level().isClientSide && strain && stats.strainHand(0.002f) && player instanceof ServerPlayer server) {
            server.displayClientMessage(Component.translatable("message.canopy.hand_hurt"), true);
        }
        if (!player.level().isClientSide && gouge && kind == Kind.VEIN) {
            stats.setStamina(Math.max(0f, stats.stamina() - 0.01f));
        }
        if (!player.level().isClientSide && machete && (kind == Kind.THICKET || kind == Kind.WOOD)) {
            stats.setStamina(Math.max(0f, stats.stamina() - (metal ? 0.008f : 0.012f)));
        }
        if (stats.hand() >= SurvivalStats.HAND_INJURED) {
            speed *= 0.45f;
        }
        if (stats.stamina() <= 0.5f) {
            speed *= 0.45f;
        }
        if (stats.temperature() < 36f || stats.temperature() > 64f) {
            speed *= 0.75f;
        }
        if (stats.wetness() > 0.8f) {
            speed *= 0.85f;
        }
        event.setNewSpeed(Math.max(0.01f, speed));
    }

    public static void afterBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player) || player.isCreative() || player.isSpectator()) {
            return;
        }
        BlockState state = event.getState();
        Kind kind = kind(state);
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            if (kind == Kind.SOIL) {
                stats.setWetness(stats.wetness() + 0.05f);
            }
        });
        if (holdingGouge(player) && kind == Kind.VEIN) {
            ItemStack gouge = player.getMainHandItem();
            gouge.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            CanopyAdvancements.award(player, "pried");
            return;
        }
        if (!holdingMachete(player) || (kind != Kind.THICKET && kind != Kind.WOOD)) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        stack.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        if (kind == Kind.THICKET) {
            CanopyAdvancements.award(player, "cut_path");
        }
    }

    private static Kind kind(BlockState state) {
        if (state.is(VEIN)) {
            return Kind.VEIN;
        }
        if (state.is(THICKET)) {
            return Kind.THICKET;
        }
        if (state.is(FOLIAGE) || state.is(BlockTags.LEAVES) || state.is(BlockTags.SAPLINGS)) {
            return Kind.FOLIAGE;
        }
        if (state.is(SOIL)) {
            return Kind.SOIL;
        }
        if (state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS) || state.is(BlockTags.WOODEN_STAIRS)
                || state.is(BlockTags.WOODEN_SLABS) || state.is(BlockTags.WOODEN_FENCES)
                || state.is(BlockTags.WOODEN_DOORS) || state.is(BlockTags.WOODEN_TRAPDOORS)
                || state.is(BlockTags.MINEABLE_WITH_AXE)) {
            return Kind.WOOD;
        }
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return Kind.HARD;
        }
        return Kind.OTHER;
    }

    private enum Kind {
        VEIN,
        FOLIAGE,
        THICKET,
        SOIL,
        WOOD,
        HARD,
        OTHER
    }
}
