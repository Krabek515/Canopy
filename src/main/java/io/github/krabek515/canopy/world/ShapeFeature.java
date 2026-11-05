package io.github.krabek515.canopy.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Фича без настроек, которая просто вызывает одну форму из {@link JungleTrees}. */
public class ShapeFeature extends Feature<NoneFeatureConfiguration> {
    @FunctionalInterface
    public interface Shape {
        boolean grow(WorldGenLevel level, BlockPos origin, RandomSource random);
    }

    private final Shape shape;

    public ShapeFeature(Codec<NoneFeatureConfiguration> codec, Shape shape) {
        super(codec);
        this.shape = shape;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return shape.grow(context.level(), context.origin(), context.random());
    }
}
