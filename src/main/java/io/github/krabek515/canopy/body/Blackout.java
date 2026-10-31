package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.network.CanopyNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public final class Blackout {
    private Blackout() {
    }

    public static void wake(ServerPlayer player, SurvivalStats stats) {
        // Обморок вместо экрана смерти: инвентарь остаётся, меняется только то, где и в каком состоянии человек очнулся.
        Difficulty difficulty = player.level().getLevelData().getDifficulty();
        boolean carry = SurvivalTuning.carryToCamp(difficulty);
        if (carry || !hasFooting(player)) {
            moveToSafety(player, carry);
        }

        player.setHealth(Math.min(player.getMaxHealth(), SurvivalTuning.wakeHealth(difficulty)));
        player.fallDistance = 0f;
        player.clearFire();
        player.stopUsingItem();
        if (player.isSleeping()) {
            player.stopSleepInBed(true, true);
        }

        BodySimulation.pullTowardComfort(stats);
        stats.setRecoveringUntil(player.level().getGameTime() + SurvivalTuning.mercyTicks(difficulty));

        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 20, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 15 * 20, 0, false, false, true));
        player.displayClientMessage(Component.translatable(carry
                ? "message.canopy.blackout.carried"
                : "message.canopy.blackout.left"), false);
        CanopyNetwork.send(player);
    }

    private static void moveToSafety(ServerPlayer player, boolean camp) {
        ServerLevel destination = player.serverLevel();
        BlockPos pos = player.blockPosition();
        float yaw = player.getYRot();
        if (camp) {
            destination = respawnLevel(player);
            pos = player.getRespawnPosition();
            yaw = player.getRespawnAngle();
            if (pos == null || destination.dimension() != Level.OVERWORLD) {
                destination = player.getServer().overworld();
                pos = destination.getSharedSpawnPos();
                yaw = destination.getSharedSpawnAngle();
            }
            pos = standableNear(destination, pos);
        } else {
            BlockPos nearby = standableNear(destination, pos);
            if (nearby == null) {
                destination = player.getServer().overworld();
                pos = standableNear(destination, destination.getSharedSpawnPos());
                yaw = destination.getSharedSpawnAngle();
            } else {
                pos = nearby;
            }
        }
        if (pos == null) {
            return;
        }
        player.teleportTo(destination, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, 0f);
    }

    private static ServerLevel respawnLevel(ServerPlayer player) {
        ServerLevel level = player.getServer().getLevel(player.getRespawnDimension());
        return level == null ? player.getServer().overworld() : level;
    }

    private static boolean hasFooting(ServerPlayer player) {
        if (player.isInLava() || player.isOnFire()) {
            return false;
        }
        BlockPos feet = player.blockPosition();
        BlockState state = player.level().getBlockState(feet);
        return state.getCollisionShape(player.level(), feet).isEmpty()
                && !player.level().getFluidState(feet).is(FluidTags.LAVA);
    }

    private static BlockPos standableNear(ServerLevel level, BlockPos origin) {
        if (origin == null) {
            return null;
        }
        if (canStand(level, origin)) {
            return origin;
        }
        for (int radius = 1; radius <= 8; radius++) {
            for (int y = -2; y <= 4; y++) {
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (Math.max(Math.abs(x), Math.abs(z)) != radius) {
                            continue;
                        }
                        BlockPos candidate = origin.offset(x, y, z);
                        if (canStand(level, candidate)) {
                            return candidate;
                        }
                    }
                }
            }
        }
        return origin.above(2);
    }

    private static boolean canStand(ServerLevel level, BlockPos feet) {
        if (!level.getWorldBorder().isWithinBounds(feet)) {
            return false;
        }
        if (level.getFluidState(feet).is(FluidTags.LAVA) || level.getFluidState(feet.above()).is(FluidTags.LAVA)) {
            return false;
        }
        CollisionContext context = CollisionContext.empty();
        boolean feetFree = level.getBlockState(feet).getCollisionShape(level, feet, context).isEmpty();
        boolean headFree = level.getBlockState(feet.above()).getCollisionShape(level, feet.above(), context).isEmpty();
        boolean ground = !level.getBlockState(feet.below()).getCollisionShape(level, feet.below(), context).isEmpty();
        return feetFree && headFree && ground;
    }

    public static boolean unavoidable(net.minecraft.world.damagesource.DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }
}
