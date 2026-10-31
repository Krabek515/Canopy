package io.github.krabek515.canopy.body;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class HeatSources {
    private HeatSources() {
    }

    public static boolean near(Player player, int radius) {
        Level level = player.level();
        BlockPos center = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -2, -radius), center.offset(radius, 2, radius))) {
            if (warms(level.getBlockState(pos))) {
                return true;
            }
        }
        return false;
    }

    private static boolean warms(BlockState state) {
        if (state.getFluidState().is(FluidTags.LAVA) || state.is(BlockTags.FIRE)) {
            return true;
        }
        if (!state.hasProperty(BlockStateProperties.LIT) || !state.getValue(BlockStateProperties.LIT)) {
            return false;
        }
        return state.is(BlockTags.CAMPFIRES)
                || state.is(Blocks.FURNACE)
                || state.is(Blocks.BLAST_FURNACE)
                || state.is(Blocks.SMOKER);
    }
}
