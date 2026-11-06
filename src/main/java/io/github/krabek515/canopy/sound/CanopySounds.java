package io.github.krabek515.canopy.sound;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Свои звуковые события зверей Canopy. Реализация — ванильные сэмплы с изменённой высотой тона (sounds.json). */
public final class CanopySounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Canopy.MOD_ID);

    public static final RegistryObject<SoundEvent> JAGUAR_AMBIENT = reg("entity.jaguar.ambient");
    public static final RegistryObject<SoundEvent> JAGUAR_HURT = reg("entity.jaguar.hurt");
    public static final RegistryObject<SoundEvent> JAGUAR_DEATH = reg("entity.jaguar.death");
    public static final RegistryObject<SoundEvent> MONKEY_AMBIENT = reg("entity.monkey.ambient");
    public static final RegistryObject<SoundEvent> MONKEY_HURT = reg("entity.monkey.hurt");
    public static final RegistryObject<SoundEvent> MONKEY_DEATH = reg("entity.monkey.death");
    public static final RegistryObject<SoundEvent> SNAKE_AMBIENT = reg("entity.snake.ambient");
    public static final RegistryObject<SoundEvent> SNAKE_HURT = reg("entity.snake.hurt");
    public static final RegistryObject<SoundEvent> SNAKE_DEATH = reg("entity.snake.death");
    public static final RegistryObject<SoundEvent> AGOUTI_AMBIENT = reg("entity.agouti.ambient");
    public static final RegistryObject<SoundEvent> AGOUTI_HURT = reg("entity.agouti.hurt");
    public static final RegistryObject<SoundEvent> AGOUTI_DEATH = reg("entity.agouti.death");
    public static final RegistryObject<SoundEvent> CAPYBARA_AMBIENT = reg("entity.capybara.ambient");
    public static final RegistryObject<SoundEvent> CAPYBARA_HURT = reg("entity.capybara.hurt");
    public static final RegistryObject<SoundEvent> CAPYBARA_DEATH = reg("entity.capybara.death");
    public static final RegistryObject<SoundEvent> TAPIR_AMBIENT = reg("entity.tapir.ambient");
    public static final RegistryObject<SoundEvent> TAPIR_HURT = reg("entity.tapir.hurt");
    public static final RegistryObject<SoundEvent> TAPIR_DEATH = reg("entity.tapir.death");
    public static final RegistryObject<SoundEvent> CAIMAN_AMBIENT = reg("entity.caiman.ambient");
    public static final RegistryObject<SoundEvent> CAIMAN_HURT = reg("entity.caiman.hurt");
    public static final RegistryObject<SoundEvent> CAIMAN_DEATH = reg("entity.caiman.death");
    public static final RegistryObject<SoundEvent> GORILLA_AMBIENT = reg("entity.gorilla.ambient");
    public static final RegistryObject<SoundEvent> GORILLA_HURT = reg("entity.gorilla.hurt");
    public static final RegistryObject<SoundEvent> GORILLA_DEATH = reg("entity.gorilla.death");
    public static final RegistryObject<SoundEvent> TOUCAN_AMBIENT = reg("entity.toucan.ambient");
    public static final RegistryObject<SoundEvent> TOUCAN_HURT = reg("entity.toucan.hurt");
    public static final RegistryObject<SoundEvent> TOUCAN_DEATH = reg("entity.toucan.death");

    private CanopySounds() {
    }

    private static RegistryObject<SoundEvent> reg(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, name)));
    }
}
