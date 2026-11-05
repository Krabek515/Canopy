package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.body.CanopyDamage;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.config.CanopyConfig;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.network.CanopyNetwork;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.levelgen.Heightmap;

public final class FlashFlood {
    private static final int WAVE_TICKS = 12 * 20;

    private FlashFlood() {
    }

    public static void consider(ServerLevel level) {
        if (Downpour.active(level)) {
            return;
        }
        long now = level.getGameTime();
        for (ServerPlayer player : level.players()) {
            if (player.isCreative() || player.isSpectator() || !player.isAlive()) {
                continue;
            }
            player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> tick(level, player, stats, now));
        }
    }

    public static void force(ServerPlayer player) {
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> warn(player.serverLevel(), player, stats, player.level().getGameTime()));
    }

    public static boolean busy(ServerPlayer player) {
        long now = player.level().getGameTime();
        return player.getCapability(CanopyCapabilities.SURVIVAL)
                .map(stats -> now < stats.floodWarnUntil() || now < stats.floodWaveUntil())
                .orElse(false);
    }

    private static void tick(ServerLevel level, ServerPlayer player, SurvivalStats stats, long now) {
        if (stats.floodWaveUntil() != 0L && now >= stats.floodWaveUntil()) {
            player.displayClientMessage(Component.translatable("message.canopy.flood_passed"), true);
            stats.setFloodWaveUntil(0L);
            stats.setFloodWarnUntil(0L);
            CanopyNetwork.send(player);
            return;
        }
        if (now < stats.floodWaveUntil()) {
            if (inWash(level, player)) {
                sweep(level, player, stats);
            }
            return;
        }
        if (stats.floodWarnUntil() != 0L && now >= stats.floodWarnUntil()) {
            release(level, player, stats, now);
            return;
        }
        if (now < stats.floodWarnUntil()) {
            rumble(level, player, stats, now);
            return;
        }
        if (stats.floodNext() == 0L) {
            stats.setFloodNext(now + firstTicks());
            return;
        }
        if (now < stats.floodNext() || !inCanyon(player) || !inWash(level, player)) {
            return;
        }
        if (level.random.nextFloat() > (WetSeason.active ? 0.75f : 0.4f)) {
            stats.setFloodNext(now + 20L * 60L * 4L);
            return;
        }
        warn(level, player, stats, now);
    }

    private static void warn(ServerLevel level, ServerPlayer player, SurvivalStats stats, long now) {
        int ticks = warningTicks();
        stats.setFloodWarnUntil(now + ticks);
        stats.setFloodNext(now + ticks + cooldownTicks());
        player.displayClientMessage(Component.translatable("message.canopy.flood_warn"), true);
        JournalLearning.note(player, Journal.FLOOD);
        level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.45f, 0.6f);
        CanopyNetwork.send(player);
    }

    private static void rumble(ServerLevel level, ServerPlayer player, SurvivalStats stats, long now) {
        long left = stats.floodWarnUntil() - now;
        if (left > 20L && left % 160L < 20L) {
            level.playSound(null, player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.35f, 0.7f);
        }
    }

    private static void release(ServerLevel level, ServerPlayer player, SurvivalStats stats, long now) {
        stats.setFloodWaveUntil(now + WAVE_TICKS);
        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_SPLASH, SoundSource.WEATHER, 1f, 0.7f);
        if (inWash(level, player)) {
            player.displayClientMessage(Component.translatable("message.canopy.flood_wave"), true);
            sweep(level, player, stats);
        } else {
            player.displayClientMessage(Component.translatable("message.canopy.flood_high"), true);
            CanopyAdvancements.award(player, "high_ground");
        }
        CanopyNetwork.send(player);
    }

    private static void sweep(ServerLevel level, ServerPlayer player, SurvivalStats stats) {
        double angle = (player.getUUID().getLeastSignificantBits() & 7L) * (Math.PI / 4.0);
        player.setDeltaMovement(Math.cos(angle) * 0.65, 0.28, Math.sin(angle) * 0.65);
        player.hurtMarked = true;
        player.fallDistance = 0f;
        stats.setWetness(stats.wetness() + 0.15f);
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false, true));
        if ((level.getGameTime() / 20L) % 2L == 0L) {
            player.hurt(CanopyDamage.source(level, CanopyDamage.FLOOD), 1f);
        }
    }

    private static boolean inCanyon(ServerPlayer player) {
        return player.level().getBiome(player.blockPosition()).unwrapKey()
                .filter(key -> Canopy.MOD_ID.equals(key.location().getNamespace()) && "canyon".equals(key.location().getPath()))
                .isPresent();
    }

    private static boolean inWash(ServerLevel level, ServerPlayer player) {
        BlockPos origin = player.blockPosition();
        int min = Integer.MAX_VALUE;
        int samples = 0;
        for (int dx = -12; dx <= 12; dx += 4) {
            for (int dz = -12; dz <= 12; dz += 4) {
                BlockPos column = origin.offset(dx, 0, dz);
                if (!level.hasChunkAt(column)) {
                    continue;
                }
                int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column.getX(), column.getZ());
                min = Math.min(min, height);
                samples++;
            }
        }
        return samples > 0 && player.getY() <= min + 3.0;
    }

    private static int warningTicks() {
        return (int) Math.round(CanopyConfig.FLOOD_WARNING_SECONDS.get() * 20.0);
    }

    private static long cooldownTicks() {
        long base = Math.round(CanopyConfig.EVENT_COOLDOWN_MINUTES.get() * 60.0 * 20.0);
        return WetSeason.active ? base / 2L : base;
    }

    private static long firstTicks() {
        return Math.max(20L * 120L, cooldownTicks() / 8L);
    }
}
