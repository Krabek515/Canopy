package io.github.krabek515.canopy.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.krabek515.canopy.Canopy;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

/**
 * Источник биомов-обёртка: весь мир отдаёт ванильному источнику (любой
 * multi_noise, в том числе чужой через preset), а в местах ванильных джунглей
 * возвращает наши биомы по климатической точке — так Canopy остаётся аддоном,
 * не ломая остальные биомы и чужие моды.
 */
public class CanopyBiomeSource extends BiomeSource {
    public static final Codec<CanopyBiomeSource> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BiomeSource.CODEC.fieldOf("delegate").forGetter(s -> s.delegate),
            Biome.CODEC.fieldOf("lowland").forGetter(s -> s.lowland),
            Biome.CODEC.fieldOf("bamboo_thicket").forGetter(s -> s.bamboo),
            Biome.CODEC.fieldOf("flooded").forGetter(s -> s.flooded),
            Biome.CODEC.fieldOf("highland").forGetter(s -> s.highland),
            Biome.CODEC.fieldOf("canyon").forGetter(s -> s.canyon)
    ).apply(inst, CanopyBiomeSource::new));

    public static final DeferredRegister<Codec<? extends BiomeSource>> SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, Canopy.MOD_ID);
    public static final RegistryObject<Codec<CanopyBiomeSource>> JUNGLE =
            SOURCES.register("jungle", () -> CODEC);

    private final BiomeSource delegate;
    private final Holder<Biome> lowland;
    private final Holder<Biome> bamboo;
    private final Holder<Biome> flooded;
    private final Holder<Biome> highland;
    private final Holder<Biome> canyon;

    public CanopyBiomeSource(BiomeSource delegate, Holder<Biome> lowland, Holder<Biome> bamboo,
                             Holder<Biome> flooded, Holder<Biome> highland, Holder<Biome> canyon) {
        this.delegate = delegate;
        this.lowland = lowland;
        this.bamboo = bamboo;
        this.flooded = flooded;
        this.highland = highland;
        this.canyon = canyon;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(delegate.possibleBiomes().stream(), Stream.of(lowland, bamboo, flooded, highland, canyon));
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        Holder<Biome> vanilla = delegate.getNoiseBiome(x, y, z, sampler);
        if (!vanilla.is(BiomeTags.IS_JUNGLE)) {
            return vanilla;
        }
        Climate.TargetPoint p = sampler.sample(x, y, z);
        return pick(p);
    }

    /** Климатическая точка -> один из пяти биомов полога. Крайние значения дают редкие места. */
    private Holder<Biome> pick(Climate.TargetPoint p) {
        if (p.erosion() < -0.25f) {
            return highland;
        }
        if (p.humidity() > 0.1f && p.erosion() >= 0.5f) {
            return flooded;
        }
        if (p.humidity() < -0.15f) {
            return bamboo;
        }
        if (p.erosion() >= 0.3f || Math.abs(p.weirdness()) > 1.35f) {
            return canyon;
        }
        return lowland;
    }
}
