package io.github.krabek515.canopy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;

public final class BeastSpawn {
    private BeastSpawn() {
    }

    public static boolean shade(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON)
                && level.getRawBrightness(pos, 0) > 4
                && level.getFluidState(pos).isEmpty();
    }

    /** Берег: земля или ил, а в шести блоках есть вода. */
    public static boolean shore(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        BlockState ground = level.getBlockState(pos.below());
        if (!(ground.is(BlockTags.ANIMALS_SPAWNABLE_ON) || ground.is(Blocks.MUD)) || !level.getFluidState(pos).isEmpty()) {
            return false;
        }
        return waterNear(level, pos, 6);
    }

    /** Мелководье: в воде, но не глубже двух блоков. */
    public static boolean shallows(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getFluidState(pos).is(FluidTags.WATER)
                && !level.getFluidState(pos.below(2)).is(FluidTags.WATER)
                && level.getRawBrightness(pos, 0) > 4;
    }

    /** Полог: на листве или рядом с ней, на свету. */
    public static boolean canopy(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        BlockState below = level.getBlockState(pos.below());
        return (below.is(BlockTags.LEAVES) || below.is(BlockTags.LOGS) || below.is(BlockTags.ANIMALS_SPAWNABLE_ON))
                && level.getRawBrightness(pos, 0) > 8
                && level.getFluidState(pos).isEmpty();
    }

    /** Ищет воду рядом, не выходя из чанка: при генерации соседний чанк ещё недоступен. */
    private static boolean waterNear(LevelAccessor level, BlockPos center, int radius) {
        int minX = Math.max(center.getX() - radius, center.getX() & ~15);
        int maxX = Math.min(center.getX() + radius, (center.getX() & ~15) + 15);
        int minZ = Math.max(center.getZ() - radius, center.getZ() & ~15);
        int maxZ = Math.min(center.getZ() + radius, (center.getZ() & ~15) + 15);
        for (BlockPos p : BlockPos.betweenClosed(minX, center.getY() - 2, minZ, maxX, center.getY() + 1, maxZ)) {
            if (level.getFluidState(p).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
}
