package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.block.CanopyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class GasPocket {
    private GasPocket() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin) {
        BlockPos seep = origin.below();
        if (!level.getBlockState(origin).isAir() || !level.getFluidState(origin).isEmpty() || !soil(level.getBlockState(seep))) {
            return false;
        }
        level.setBlock(seep, CanopyBlocks.GAS_SEEP.get().defaultBlockState(), 2);
        BlockPos plant = origin.offset(5, 0, 0);
        if (level.getBlockState(plant).isAir() && soil(level.getBlockState(plant.below()))) {
            level.setBlock(plant, CanopyBlocks.AIR_PLANT.get().defaultBlockState(), 2);
        }
        return true;
    }

    public static boolean force(LevelAccessor level, BlockPos feet) {
        BlockPos seep = feet.below();
        if (level.getBlockState(seep).isAir()) {
            return false;
        }
        level.setBlock(seep, CanopyBlocks.GAS_SEEP.get().defaultBlockState(), 3);
        BlockPos plant = feet.offset(5, 0, 0);
        if (level.getBlockState(plant).isAir()) {
            level.setBlock(plant, CanopyBlocks.AIR_PLANT.get().defaultBlockState(), 3);
        }
        return true;
    }

    private static boolean soil(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(Blocks.MUD) || state.is(Blocks.CLAY);
    }
}
