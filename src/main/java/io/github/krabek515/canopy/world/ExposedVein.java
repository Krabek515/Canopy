package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.block.CanopyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class ExposedVein {
    private ExposedVein() {
    }

    public static BlockPos find(LevelAccessor level, BlockPos origin) {
        for (int dy = 0; dy >= -12; dy--) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    if (stone(level.getBlockState(pos)) && exposed(level, pos)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    public static boolean grow(LevelAccessor level, BlockPos origin) {
        BlockPos spot = find(level, origin);
        if (spot == null) {
            return false;
        }
        level.setBlock(spot, CanopyBlocks.EXPOSED_VEIN.get().defaultBlockState(), 2);
        return true;
    }

    public static boolean force(LevelAccessor level, BlockPos pos) {
        if (level.getBlockState(pos).isAir()) {
            return false;
        }
        level.setBlock(pos, CanopyBlocks.EXPOSED_VEIN.get().defaultBlockState(), 3);
        return true;
    }

    private static boolean stone(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(Blocks.SANDSTONE) || state.is(Blocks.CALCITE) || state.is(Blocks.DRIPSTONE_BLOCK);
    }

    private static boolean exposed(LevelAccessor level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) {
                continue;
            }
            BlockState side = level.getBlockState(pos.relative(direction));
            if (side.isAir() && side.getFluidState().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
