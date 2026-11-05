package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.item.WornClothing;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;

public final class BodySimulation {
    private BodySimulation() {
    }

    public static void tickSecond(ServerPlayer player, SurvivalStats stats) {
        Level level = player.level();
        Difficulty difficulty = level.getLevelData().getDifficulty();
        BlockPos pos = player.blockPosition();
        boolean jungle = JungleClimate.inJungle(player);
        boolean openSky = level.getBrightness(LightLayer.SKY, pos) >= 15;
        boolean raining = level.isRainingAt(pos);

        float ambient;
        if (jungle) {
            ambient = level.isDay() ? 68f : 54f;
            if (raining) {
                ambient -= 10f;
            }
            if (!openSky) {
                ambient -= 6f;
            }
            if (player.isInWater()) {
                ambient -= 16f;
            }
        } else {
            ambient = 50f;
        }
        if (stats.nearHeat()) {
            ambient += 22f;
        }
        ambient += WornClothing.heat(player);
        if (stats.wetness() > 0.45f) {
            ambient -= 6f + stats.wetness() * 8f;
        }

        float body = stats.temperature();
        body += (ambient - body) * (jungle ? 0.12f : 0.3f);
        if (player.isSprinting() && jungle) {
            body += 0.6f;
        }
        stats.setTemperature(body);

        float wet = stats.wetness();
        float wetGain = WornClothing.wetGain(player);
        float drySpeed = WornClothing.drySpeed(player);
        if (player.isInWater()) {
            wet += 0.18f * wetGain;
        } else if (raining) {
            wet += 0.06f * wetGain;
        } else {
            wet -= (stats.nearHeat() ? 0.1f : 0.02f) * drySpeed;
        }
        if (stats.nearHeat() && !player.isInWater()) {
            wet -= 0.04f * drySpeed;
        }
        stats.setWetness(wet);

        long now = level.getGameTime();
        WoundCare.tick(player, stats);
        Gas.tick(player);
        if (!stats.isRecovering(now)) {
            if (jungle) {
                applyHeatStrain(player, stats);
                if (player.tickCount % SurvivalTuning.hurtIntervalTicks(difficulty) == 0) {
                    hurt(player, stats, difficulty);
                }
            }
        }
    }

    private static void applyHeatStrain(ServerPlayer player, SurvivalStats stats) {
        if (stats.temperature() > 64f || stats.wetness() > 0.9f) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, true));
        }
        if (stats.temperature() > 72f) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false, true));
        }
    }

    private static void hurt(ServerPlayer player, SurvivalStats stats, Difficulty difficulty) {
        if (!SurvivalTuning.hurts(difficulty)) {
            return;
        }
        if (stats.temperature() >= 76f) {
            player.hurt(CanopyDamage.source(player.level(), CanopyDamage.HEAT), SurvivalTuning.hurtAmount(difficulty));
        }
    }

    public static void pullTowardComfort(SurvivalStats stats) {
        stats.setTemperature(Mth.lerp(0.45f, stats.temperature(), 50f));
    }
}
