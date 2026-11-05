package io.github.krabek515.canopy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Орхидея-эпифит: сидит на стволах и мхе, цветёт пурпурным — источник красителя. */
public class OrchidBlock extends BushBlock {
    public OrchidBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.LOGS) || state.is(Blocks.MOSS_BLOCK) || state.is(BlockTags.DIRT);
    }
}
