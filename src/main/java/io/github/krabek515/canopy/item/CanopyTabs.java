package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class CanopyTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Canopy.MOD_ID);
    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.canopy"))
            .icon(() -> FlaskItem.withWater(FlaskWater.CLEAN))
            .displayItems((parameters, output) -> {
                output.accept(FlaskItem.withWater(FlaskWater.EMPTY));
                output.accept(FlaskItem.withWater(FlaskWater.MURKY));
                output.accept(FlaskItem.withWater(FlaskWater.CLEAN));
            })
            .build());

    private CanopyTabs() {
    }
}
