package io.github.krabek515.canopy.entity;

import io.github.krabek515.canopy.body.HeatSources;
import io.github.krabek515.canopy.config.CanopyConfig;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import io.github.krabek515.canopy.world.Downpour;
import io.github.krabek515.canopy.world.FlashFlood;
import io.github.krabek515.canopy.world.Shelter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import io.github.krabek515.canopy.sound.CanopySounds;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public final class MonkeyRaid {
    public static final String NEXT = "CanopyRaidNext";
    public static final String WAVE = "CanopyRaidWave";
    public static final String DONE = "CanopyRaidDone";

    private MonkeyRaid() {
    }

    public static void consider(ServerLevel level) {
        if (Downpour.active(level)) {
            return;
        }
        long now = level.getGameTime();
        for (ServerPlayer player : level.players()) {
            if (player.isCreative() || player.isSpectator() || !player.isAlive() || FlashFlood.busy(player)) {
                continue;
            }
            long next = player.getPersistentData().getLong(NEXT);
            if (next == 0L) {
                player.getPersistentData().putLong(NEXT, now + firstTicks());
                continue;
            }
            if (now < next || !ready(player)) {
                continue;
            }
            if (level.random.nextFloat() > 0.4f) {
                player.getPersistentData().putLong(NEXT, now + 20L * 60L * 4L);
                continue;
            }
            begin(level, player);
        }
    }

    public static void begin(ServerLevel level, ServerPlayer player) {
        long now = level.getGameTime();
        player.getPersistentData().putLong(WAVE, now);
        player.getPersistentData().putBoolean(DONE, false);
        player.getPersistentData().putLong(NEXT, now + cooldownTicks());
        player.displayClientMessage(Component.translatable("message.canopy.raid"), true);
        JournalLearning.note(player, Journal.MONKEY);
        level.playSound(null, player.blockPosition(), CanopySounds.MONKEY_AMBIENT.get(), SoundSource.NEUTRAL, 0.8f, 1.5f);
        for (int i = 0; i < 3; i++) {
            spawn(level, player, now, i);
        }
    }

    public static void snatch(Monkey monkey, ServerPlayer player) {
        if (player.getPersistentData().getLong(WAVE) != monkey.wave()) {
            monkey.flee();
            return;
        }
        if (!player.getPersistentData().getBoolean(DONE)) {
            player.getPersistentData().putBoolean(DONE, true);
            ItemStack stolen = take(player);
            if (stolen.isEmpty()) {
                player.displayClientMessage(Component.translatable("message.canopy.raid_empty"), true);
            } else {
                monkey.setItemSlot(EquipmentSlot.MAINHAND, stolen);
                monkey.setDropChance(EquipmentSlot.MAINHAND, 2.0f);
                player.displayClientMessage(Component.translatable("message.canopy.stolen", stolen.getHoverName(), stolen.getCount()), true);
                JournalLearning.note(player, Journal.MONKEY_RAID);
                CanopyAdvancements.award(player, "light_fingers");
            }
        }
        scatter(monkey, player);
    }

    private static void scatter(Monkey monkey, ServerPlayer player) {
        AABB box = player.getBoundingBox().inflate(24);
        for (Monkey other : player.level().getEntitiesOfClass(Monkey.class, box)) {
            if (other.wave() == monkey.wave()) {
                other.flee();
            }
        }
    }

    private static void spawn(ServerLevel level, ServerPlayer player, long wave, int index) {
        Monkey monkey = CanopyEntities.MONKEY.get().create(level);
        if (monkey == null) {
            return;
        }
        double angle = (Math.PI * 2.0 / 3.0) * index;
        double x = player.getX() + Math.cos(angle) * 11.0;
        double z = player.getZ() + Math.sin(angle) * 11.0;
        BlockPos pos = BlockPos.containing(x, player.getY(), z);
        if (!level.isLoaded(pos)) {
            return;
        }
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        monkey.moveTo(x, y, z, player.getYRot(), 0f);
        monkey.beginRaid(wave, player.getUUID());
        level.addFreshEntity(monkey);
    }

    private static boolean ready(ServerPlayer player) {
        return hasLoot(player) && (HeatSources.near(player, 5) || Shelter.underLeaves(player));
    }

    private static boolean hasLoot(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (ItemStack stack : inventory.items) {
            if (stealable(stack)) {
                return true;
            }
        }
        return stealable(player.getOffhandItem());
    }

    private static ItemStack take(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        int edible = -1;
        int tool = -1;
        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack stack = inventory.items.get(i);
            if (!stealable(stack)) {
                continue;
            }
            if (stack.getItem().isEdible()) {
                if (edible < 0 || stack.getCount() > inventory.items.get(edible).getCount()) {
                    edible = i;
                }
            } else if (tool < 0) {
                tool = i;
            }
        }
        int slot = edible >= 0 ? edible : tool;
        if (slot >= 0) {
            ItemStack stolen = inventory.items.get(slot).copy();
            inventory.setItem(slot, ItemStack.EMPTY);
            return stolen;
        }
        ItemStack off = player.getOffhandItem();
        if (!stealable(off)) {
            return ItemStack.EMPTY;
        }
        ItemStack stolen = off.copy();
        player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        return stolen;
    }

    private static boolean stealable(ItemStack stack) {
        if (stack.isEmpty() || stack.is(CanopyItems.DIARY.get())) {
            return false;
        }
        return stack.getItem().isEdible() || stack.is(CanopyItems.STONE_MACHETE.get()) || stack.is(CanopyItems.FLASK.get());
    }

    private static long cooldownTicks() {
        return Math.round(CanopyConfig.EVENT_COOLDOWN_MINUTES.get() * 60.0 * 20.0);
    }

    private static long firstTicks() {
        return Math.max(20L * 120L, cooldownTicks() / 8L);
    }
}
