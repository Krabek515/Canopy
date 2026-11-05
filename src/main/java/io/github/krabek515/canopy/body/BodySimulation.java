package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.item.WornClothing;
import io.github.krabek515.canopy.config.CanopyConfig;
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
        float drain = SurvivalTuning.drain(difficulty);
        BlockPos pos = player.blockPosition();
        boolean openSky = level.getBrightness(LightLayer.SKY, pos) >= 15;
        boolean raining = level.isRainingAt(pos);

        float ambient = level.isDay() ? 64f : 40f;
        if (raining) {
            ambient -= 12f;
        }
        if (!openSky) {
            ambient -= 8f;
        }
        if (player.isInWater()) {
            ambient -= 14f;
        }
        if (stats.nearHeat()) {
            ambient += 22f;
        }
        ambient += WornClothing.heat(player);
        if (stats.wetness() > 0.45f) {
            ambient -= 6f + stats.wetness() * 8f;
        }

        float body = stats.temperature();
        body += (ambient - body) * 0.12f;
        if (player.isSprinting()) {
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

        float thirstLoss = 20f / (float) (CanopyConfig.THIRST_MINUTES.get() * 60.0);
        if (player.isSprinting()) {
            thirstLoss *= 1.7f;
        }
        if (stats.temperature() > 66f) {
            thirstLoss *= 1.45f;
        } else if (stats.temperature() < 30f) {
            thirstLoss *= 0.8f;
        }
        if (stats.wound() == SurvivalStats.WOUND_FEVER) {
            thirstLoss *= 1.55f;
        }
        stats.setThirst(stats.thirst() - thirstLoss * drain);

        long now = level.getGameTime();
        WoundCare.tick(player, stats);
        Gas.tick(player);
        if (!stats.isRecovering(now)) {
            applyStrain(player, stats);
            if (player.tickCount % SurvivalTuning.hurtIntervalTicks(difficulty) == 0) {
                hurt(player, stats, difficulty);
            }
        }
    }

    public static void tickStamina(ServerPlayer player, SurvivalStats stats) {
        float drain = SurvivalTuning.drain(player.level().getLevelData().getDifficulty());
        if (player.isSprinting() && stats.stamina() > 0f) {
            float cost = 0.05f;
            if (stats.temperature() > 66f) {
                cost += 0.03f;
            }
            if (stats.wetness() > 0.7f) {
                cost += 0.02f;
            }
            cost += WornClothing.sprintTax(player);
            stats.setStamina(stats.stamina() - cost * drain);
        } else if (stats.stamina() < SurvivalStats.NEED_MAX) {
            float regen = 0.025f;
            if (stats.nearHeat() && stats.wetness() < 0.35f && !player.isSprinting()) {
                regen += 0.02f;
            }
            stats.setStamina(stats.stamina() + regen);
        }
        if (stats.stamina() <= 0f) {
            player.setSprinting(false);
        }
    }

    private static void applyStrain(ServerPlayer player, SurvivalStats stats) {
        boolean slow = stats.temperature() < 36f || stats.temperature() > 64f || stats.thirst() <= 6f || stats.wetness() > 0.85f;
        if (slow) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, true));
        }
        if (stats.temperature() < 28f || stats.temperature() > 72f || stats.thirst() <= 0f) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false, true));
        }
    }

    private static void hurt(ServerPlayer player, SurvivalStats stats, Difficulty difficulty) {
        if (!SurvivalTuning.hurts(difficulty)) {
            return;
        }
        float amount = SurvivalTuning.hurtAmount(difficulty);
        Level level = player.level();
        if (stats.thirst() <= 0f) {
            player.hurt(CanopyDamage.source(level, CanopyDamage.THIRST), amount);
        }
        if (stats.temperature() <= 24f) {
            player.hurt(CanopyDamage.source(level, CanopyDamage.COLD), amount);
        } else if (stats.temperature() >= 76f) {
            player.hurt(CanopyDamage.source(level, CanopyDamage.HEAT), amount);
        }
    }

    public static void pullTowardComfort(SurvivalStats stats) {
        stats.setTemperature(Mth.lerp(0.45f, stats.temperature(), 50f));
        stats.setThirst(Math.max(stats.thirst(), 6f));
        stats.setStamina(Math.min(stats.stamina(), 5f));
    }
}
