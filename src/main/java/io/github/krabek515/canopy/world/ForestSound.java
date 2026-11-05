package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.body.Gas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public final class ForestSound {
    private ForestSound() {
    }

    public static void tick(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        RandomSource random = player.getRandom();
        BlockPos feet = player.blockPosition();
        if (Gas.bad(player)) {
            if (random.nextFloat() < 0.22f) {
                level.playSound(null, feet, SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT, 0.16f, 0.5f);
            }
            return;
        }
        if (level.isRainingAt(feet) && Shelter.underLeaves(player)) {
            if (random.nextFloat() < 0.4f) {
                level.playSound(null, feet.above(2), SoundEvents.POINTED_DRIPSTONE_DRIP_WATER, SoundSource.WEATHER, 0.3f, 1.2f);
            }
            return;
        }
        if (!level.isDay()) {
            boolean flooded = flooded(player);
            SoundEvent insect = flooded ? SoundEvents.FROG_AMBIENT : SoundEvents.VINE_STEP;
            if (random.nextFloat() < 0.18f) {
                level.playSound(null, aside(player, random, 1), insect, SoundSource.AMBIENT, flooded ? 0.22f : 0.1f, flooded ? 0.85f : 1.7f);
            }
            return;
        }
        if (random.nextFloat() < 0.14f) {
            level.playSound(null, aside(player, random, 6), SoundEvents.PARROT_AMBIENT, SoundSource.AMBIENT, 0.32f, 0.85f + random.nextFloat() * 0.4f);
        }
    }

    private static boolean flooded(ServerPlayer player) {
        return player.level().getBiome(player.blockPosition()).unwrapKey()
                .map(key -> {
                    ResourceLocation id = key.location();
                    return Canopy.MOD_ID.equals(id.getNamespace()) && "flooded".equals(id.getPath());
                })
                .orElse(false);
    }

    private static BlockPos aside(ServerPlayer player, RandomSource random, int up) {
        double angle = random.nextDouble() * Math.PI * 2.0;
        double distance = 4.0 + random.nextDouble() * 7.0;
        return BlockPos.containing(player.getX() + Math.cos(angle) * distance, player.getY() + up, player.getZ() + Math.sin(angle) * distance);
    }
}
