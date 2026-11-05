package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.block.CanopyBlocks;
import io.github.krabek515.canopy.block.DeckKind;
import io.github.krabek515.canopy.block.RottenDeckBlock;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class RottenDeckFeature extends Feature<NoneFeatureConfiguration> {
    public RottenDeckFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        if (!covered(level, origin)) {
            return false;
        }
        DeckKind kind = DeckKind.values()[random.nextInt(DeckKind.values().length)];
        BlockState deck = CanopyBlocks.ROTTEN_DECK.get().defaultBlockState().setValue(RottenDeckBlock.KIND, kind);
        BlockState stakes = Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(BlockStateProperties.VERTICAL_DIRECTION, Direction.UP)
                .setValue(BlockStateProperties.DRIPSTONE_THICKNESS, DripstoneThickness.TIP);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos ground = origin.offset(dx, -1, dz);
                level.setBlock(ground, deck, 2);
                level.setBlock(ground.below(), Blocks.AIR.defaultBlockState(), 2);
                BlockPos floor = ground.below(3);
                BlockState bottom = switch (kind) {
                    case BOG -> Blocks.MUD.defaultBlockState();
                    case STAKES -> stakes;
                    case SNAKE -> Blocks.DIRT.defaultBlockState();
                };
                level.setBlock(floor, bottom, 2);
                if (kind == DeckKind.BOG) {
                    level.setBlock(ground.below(2), Blocks.WATER.defaultBlockState(), 2);
                } else {
                    level.setBlock(ground.below(2), Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
        return true;
    }

    private static boolean covered(WorldGenLevel level, BlockPos origin) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos air = origin.offset(dx, 0, dz);
                if (!level.getBlockState(air).isAir() || !level.getFluidState(air).isEmpty()) {
                    return false;
                }
                BlockPos ground = air.below();
                BlockState soil = level.getBlockState(ground);
                if (!soil.is(BlockTags.DIRT) && !soil.is(Blocks.MUD) && !soil.is(Blocks.CLAY)) {
                    return false;
                }
                for (int dy = 1; dy <= 3; dy++) {
                    BlockState under = level.getBlockState(ground.below(dy));
                    if (under.isAir() || !under.getFluidState().isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
