package io.github.krabek515.canopy.body;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

/** Жара и сырость работают только в джунглях — наших биомах и ванильных тоже. */
public final class JungleClimate {
    private static final TagKey<Biome> IS_JUNGLE = TagKey.create(Registries.BIOME, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "is_jungle"));

    private JungleClimate() {
    }

    public static boolean inJungle(Player player) {
        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        return biome.is(IS_JUNGLE);
    }
}
