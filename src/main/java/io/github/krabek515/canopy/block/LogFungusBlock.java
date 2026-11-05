package io.github.krabek515.canopy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Трутовик: растёт на бревне, упавшем стволе и сырой подстилке. */
public class LogFungusBlock extends BushBlock {
    public LogFungusBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.DIRT) || state.is(Blocks.MUD) || state.is(Blocks.MOSS_BLOCK);
    }
}
