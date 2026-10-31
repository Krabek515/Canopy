package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public final class CanopyDamage {
    public static final ResourceKey<DamageType> THIRST = key("thirst");
    public static final ResourceKey<DamageType> HEAT = key("heat");
    public static final ResourceKey<DamageType> COLD = key("cold");

    private CanopyDamage() {
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> type) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type));
    }

    private static ResourceKey<DamageType> key(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, path));
    }
}
