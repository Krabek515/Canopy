package io.github.krabek515.canopy.world;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ExpeditionCampFeature extends Feature<NoneFeatureConfiguration> {
    public ExpeditionCampFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return ExpeditionCamp.grow(context.level(), context.origin());
    }
}
