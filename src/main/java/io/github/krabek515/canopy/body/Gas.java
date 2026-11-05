package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.block.CanopyBlocks;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;

public final class Gas {
    private Gas() {
    }

    public static boolean bad(Player player) {
        if (player.isSpectator() || nearPlant(player)) {
            return false;
        }
        return underground(player) || nearSeep(player);
    }

    public static boolean filtered(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        return head.is(CanopyItems.RESPIRATOR.get()) && head.getDamageValue() < head.getMaxDamage();
    }

    public static void tick(ServerPlayer player) {
        if (!bad(player)) {
            return;
        }
        JournalLearning.note(player, Journal.GAS);
        if (filtered(player)) {
            ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
            head.hurtAndBreak(1, player, broken -> broken.broadcastBreakEvent(EquipmentSlot.HEAD));
            CanopyAdvancements.award(player, "filtered");
            if (player.tickCount % 80 == 0) {
                player.displayClientMessage(Component.translatable("message.canopy.gas_filter"), true);
            }
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, false, true));
        if (player.tickCount % 40 != 0) {
            return;
        }
        Level level = player.level();
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_BREATH, SoundSource.PLAYERS, 0.35f, 0.6f);
        player.displayClientMessage(Component.translatable("message.canopy.gas"), true);
        if (!SurvivalTuning.hurts(level.getLevelData().getDifficulty())) {
            return;
        }
        player.hurt(CanopyDamage.source(level, CanopyDamage.GAS), SurvivalTuning.hurtAmount(level.getLevelData().getDifficulty()));
    }

    private static boolean underground(Player player) {
        BlockPos pos = player.blockPosition();
        int surface = player.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        if (player.getY() + 6 >= surface) {
            return false;
        }
        return player.level().getBrightness(LightLayer.SKY, pos) == 0;
    }

    private static boolean nearSeep(Player player) {
        return near(player, CanopyBlocks.GAS_SEEP.get(), 3, 2);
    }

    private static boolean nearPlant(Player player) {
        return near(player, CanopyBlocks.AIR_PLANT.get(), 2, 2);
    }

    private static boolean near(Player player, net.minecraft.world.level.block.Block block, int horizontal, int vertical) {
        BlockPos center = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-horizontal, -vertical, -horizontal), center.offset(horizontal, vertical, horizontal))) {
            if (player.level().getBlockState(pos).is(block)) {
                return true;
            }
        }
        return false;
    }
}
