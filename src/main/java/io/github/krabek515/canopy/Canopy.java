package io.github.krabek515.canopy;

import com.mojang.logging.LogUtils;
import io.github.krabek515.canopy.block.CanopyBlocks;
import io.github.krabek515.canopy.body.CanopyEffects;
import io.github.krabek515.canopy.config.CanopyConfig;
import io.github.krabek515.canopy.entity.CanopyEntities;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.world.CanopyFeatures;
import io.github.krabek515.canopy.item.CanopyTabs;
import io.github.krabek515.canopy.network.CanopyNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Canopy.MOD_ID)
public class Canopy {
    public static final String MOD_ID = "canopy";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Canopy(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.COMMON, CanopyConfig.SPEC);
        CanopyBlocks.BLOCKS.register(modBus);
        CanopyFeatures.FEATURES.register(modBus);
        CanopyItems.ITEMS.register(modBus);
        CanopyEntities.ENTITIES.register(modBus);
        io.github.krabek515.canopy.item.CanopyRecipes.SERIALIZERS.register(modBus);
        io.github.krabek515.canopy.world.CanopyBiomeSource.SOURCES.register(modBus);
        CanopyEffects.EFFECTS.register(modBus);
        io.github.krabek515.canopy.sound.CanopySounds.SOUNDS.register(modBus);
        CanopyTabs.TABS.register(modBus);
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(CanopyEntities::registerSpawns);
        CanopyNetwork.register();
        LOGGER.info("Canopy common setup");
    }
}
