package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.block.CanopyBlocks;
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
                output.accept(CanopyItems.DIARY.get());
                output.accept(FlaskItem.withWater(FlaskWater.EMPTY));
                output.accept(FlaskItem.withWater(FlaskWater.MURKY));
                output.accept(FlaskItem.withWater(FlaskWater.FRESH));
                output.accept(FlaskItem.withWater(FlaskWater.CLEAN));
                output.accept(CanopyItems.STONE_MACHETE.get());
                output.accept(CanopyItems.METAL_MACHETE.get());
                output.accept(CanopyItems.GOUGE.get());
                output.accept(CanopyItems.NUGGET.get());
                output.accept(CanopyItems.TARO_ROOT.get());
                output.accept(CanopyItems.COOKED_TARO.get());
                output.accept(CanopyItems.RAW_MEAT.get());
                output.accept(CanopyItems.COOKED_MEAT.get());
                output.accept(CanopyItems.SPOILED_MEAT.get());
                output.accept(CanopyItems.BANDAGE.get());
                output.accept(CanopyItems.RESPIRATOR.get());
                output.accept(CanopyItems.BITTER_HERB.get());
                output.accept(CanopyItems.BAD_BREW.get());
                output.accept(CanopyItems.ANTIDOTE.get());
                output.accept(CanopyItems.HIDE.get());
                output.accept(CanopyItems.FIBER_HOOD.get());
                output.accept(CanopyItems.FIBER_TUNIC.get());
                output.accept(CanopyItems.FIBER_LEGGINGS.get());
                output.accept(CanopyItems.FIBER_SANDALS.get());
                output.accept(CanopyItems.HIDE_HOOD.get());
                output.accept(CanopyItems.HIDE_COAT.get());
                output.accept(CanopyItems.HIDE_LEGGINGS.get());
                output.accept(CanopyItems.HIDE_BOOTS.get());
                output.accept(CanopyItems.JAGUAR_SPAWN_EGG.get());
                output.accept(CanopyItems.AGOUTI_SPAWN_EGG.get());
                output.accept(CanopyItems.SNAKE_SPAWN_EGG.get());
                output.accept(CanopyItems.MONKEY_SPAWN_EGG.get());
                output.accept(CanopyItems.CAPYBARA_SPAWN_EGG.get());
                output.accept(CanopyItems.TAPIR_SPAWN_EGG.get());
                output.accept(CanopyItems.CAIMAN_SPAWN_EGG.get());
                output.accept(CanopyItems.GORILLA_SPAWN_EGG.get());
                output.accept(CanopyItems.TOUCAN_SPAWN_EGG.get());
                output.accept(CanopyItems.FEATHER_TUFT.get());
                output.accept(CanopyItems.BONE_SHARD.get());
                output.accept(CanopyItems.BONE_MACHETE.get());
                output.accept(CanopyItems.RUBBER_SAP.get());
                output.accept(CanopyBlocks.RUBBER_KNOT.get());
                output.accept(CanopyBlocks.CLAY_OVEN.get());
                output.accept(CanopyItems.STEW.get());
                output.accept(CanopyBlocks.STILT_PLATFORM.get());
                output.accept(CanopyBlocks.HAMMOCK.get());
                output.accept(CanopyBlocks.THORNY_BRUSH.get());
                output.accept(CanopyBlocks.ROTTEN_DECK.get());
                output.accept(CanopyBlocks.MARKED_CARGO.get());
                output.accept(CanopyItems.SILK.get());
                output.accept(CanopyBlocks.SILK_POD.get());
                output.accept(CanopyBlocks.ROPE.get());
                output.accept(CanopyBlocks.EXPOSED_VEIN.get());
                output.accept(CanopyBlocks.EXPEDITION_CACHE.get());
                output.accept(CanopyBlocks.GAS_SEEP.get());
                output.accept(CanopyBlocks.AIR_PLANT.get());
                output.accept(CanopyBlocks.HELICONIA.get());
                output.accept(CanopyBlocks.GINGER_LILY.get());
                output.accept(CanopyBlocks.BROMELIAD.get());
                output.accept(CanopyBlocks.LOG_FUNGUS.get());
                output.accept(CanopyBlocks.WATER_LIANA.get());
            })
            .build());

    private CanopyTabs() {
    }
}
