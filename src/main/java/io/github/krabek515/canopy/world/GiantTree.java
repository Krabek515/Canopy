package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.block.CanopyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public final class GiantTree {
    private GiantTree() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 16 + random.nextInt(5);
        if (origin.getY() < level.getMinBuildHeight() + 2 || origin.getY() + height + 1 >= level.getMaxBuildHeight()) {
            return false;
        }
        if (!soil(level, origin) || !trunkClear(level, origin, height)) {
            return false;
        }
        BlockState logY = Blocks.JUNGLE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        BlockState logX = Blocks.JUNGLE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X);
        BlockState logZ = Blocks.JUNGLE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Z);
        BlockState leaves = Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        disk(level, origin, height / 2, 2, leaves, random);
        disk(level, origin, height - 5, 3, leaves, random);
        disk(level, origin, height - 3, 4, leaves, random);
        disk(level, origin, height - 1, 3, leaves, random);
        disk(level, origin, height + 1, 2, leaves, random);
        for (int y = 0; y < height; y++) {
            for (int dx = 0; dx <= 1; dx++) {
                for (int dz = 0; dz <= 1; dz++) {
                    level.setBlock(origin.offset(dx, y, dz), logY, 2);
                }
            }
        }
        buttress(level, origin, logX, -1, 0);
        buttress(level, origin, logX, -2, 0);
        buttress(level, origin, logX, 2, 0);
        buttress(level, origin, logX, 3, 1);
        buttress(level, origin, logZ, 0, -1);
        buttress(level, origin, logZ, 0, -2);
        buttress(level, origin, logZ, 1, 2);
        buttress(level, origin, logZ, 0, 3);
        level.setBlock(origin.offset(2, height - 1, 0), logX, 2);
        level.setBlock(origin.offset(3, height - 1, 1), logX, 2);
        level.setBlock(origin.offset(0, height - 1, 2), logZ, 2);
        level.setBlock(origin.offset(1, height - 1, 3), logZ, 2);
        hang(level, origin, height);
        level.setBlock(origin.offset(0, height, 1), CanopyBlocks.SILK_POD.get().defaultBlockState(), 2);
        return true;
    }

    private static void buttress(LevelAccessor level, BlockPos origin, BlockState log, int dx, int dz) {
        BlockPos pos = origin.offset(dx, 0, dz);
        if (open(level, pos)) {
            level.setBlock(pos, log, 2);
        }
    }

    private static void hang(LevelAccessor level, BlockPos origin, int height) {
        for (int y = 2; y < height - 2; y += 2) {
            vine(level, origin.offset(-1, y, 0), VineBlock.EAST);
            vine(level, origin.offset(-1, y, 1), VineBlock.EAST);
            vine(level, origin.offset(2, y, 0), VineBlock.WEST);
            vine(level, origin.offset(2, y, 1), VineBlock.WEST);
            vine(level, origin.offset(0, y, -1), VineBlock.SOUTH);
            vine(level, origin.offset(1, y, -1), VineBlock.SOUTH);
            vine(level, origin.offset(0, y, 2), VineBlock.NORTH);
            vine(level, origin.offset(1, y, 2), VineBlock.NORTH);
        }
    }

    private static void vine(LevelAccessor level, BlockPos pos, BooleanProperty face) {
        if (open(level, pos) && level.getFluidState(pos).isEmpty()) {
            level.setBlock(pos, Blocks.VINE.defaultBlockState().setValue(face, true), 2);
        }
    }

    private static void disk(LevelAccessor level, BlockPos origin, int y, int radius, BlockState leaves, RandomSource random) {
        int reach = radius * radius;
        for (int dx = -radius; dx <= radius + 1; dx++) {
            for (int dz = -radius; dz <= radius + 1; dz++) {
                if (dx >= 0 && dx <= 1 && dz >= 0 && dz <= 1) {
                    continue;
                }
                double ddx = dx - 0.5;
                double ddz = dz - 0.5;
                double dist = ddx * ddx + ddz * ddz;
                if (dist > reach || (dist > reach - 3 && random.nextInt(3) == 0)) {
                    continue;
                }
                BlockPos pos = origin.offset(dx, y, dz);
                if (open(level, pos)) {
                    level.setBlock(pos, leaves, 2);
                }
            }
        }
    }

    private static boolean soil(LevelAccessor level, BlockPos origin) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                BlockState ground = level.getBlockState(origin.offset(dx, -1, dz));
                if (!ground.is(BlockTags.DIRT) && !ground.is(Blocks.MUD) && !ground.is(Blocks.CLAY)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean trunkClear(LevelAccessor level, BlockPos origin, int height) {
        for (int y = 0; y < height; y++) {
            for (int dx = 0; dx <= 1; dx++) {
                for (int dz = 0; dz <= 1; dz++) {
                    if (!open(level, origin.offset(dx, y, dz))) {
                        return false;
                    }
                }
            }
            if (y > 0 && !open(level, origin.offset(-1, y, 0))) {
                return false;
            }
        }
        return open(level, origin.offset(2, height - 1, 0))
                && open(level, origin.offset(2, height - 1, 1))
                && open(level, origin.offset(0, height - 1, 2))
                && open(level, origin.offset(1, height - 1, 2))
                && open(level, origin.offset(0, height, 1));
    }

    private static boolean open(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir()
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.REPLACEABLE)
                || state.is(Blocks.VINE)
                || state.is(CanopyBlocks.THORNY_BRUSH.get())
                || state.is(CanopyBlocks.TARO.get())
                || state.is(CanopyBlocks.BITTER_HERB.get());
    }
}
