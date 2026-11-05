package io.github.krabek515.canopy.world;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class UndergrowthFeature extends Feature<NoneFeatureConfiguration> {
    public UndergrowthFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        ChunkAccess chunk = context.level().getChunk(context.origin());
        ForestDress.dress(chunk);
        return true;
    }
}
