package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.block.CanopyBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class MarkedCargoFeature extends Feature<NoneFeatureConfiguration> {
    public MarkedCargoFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockState here = level.getBlockState(origin);
        if (!here.isAir() || !here.getFluidState().isEmpty()) {
            return false;
        }
        BlockState ground = level.getBlockState(origin.below());
        if (!ground.is(BlockTags.DIRT) && !ground.is(Blocks.MUD) && !ground.is(Blocks.CLAY)) {
            return false;
        }
        BlockState above = level.getBlockState(origin.above());
        if (!above.isAir() && !above.is(BlockTags.LEAVES) && !above.is(BlockTags.REPLACEABLE)) {
            return false;
        }
        level.setBlock(origin, CanopyBlocks.MARKED_CARGO.get().defaultBlockState(), 2);
        return true;
    }
}
