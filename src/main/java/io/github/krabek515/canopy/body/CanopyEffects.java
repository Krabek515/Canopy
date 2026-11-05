package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CanopyEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Canopy.MOD_ID);
    public static final RegistryObject<MobEffect> VENOM = EFFECTS.register("venom", VenomEffect::new);

    private CanopyEffects() {
    }
}
