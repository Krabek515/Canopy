package io.github.krabek515.canopy.capability;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CanopyCapabilities {
    public static final Capability<SurvivalStats> SURVIVAL = CapabilityManager.get(new CapabilityToken<>() {
    });

    private CanopyCapabilities() {
    }

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(SurvivalStats.class);
    }

    @Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
    public static final class Attacher {
        private Attacher() {
        }

        @SubscribeEvent
        public static void attach(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                event.addCapability(ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "survival"), new SurvivalStatsProvider());
            }
        }
    }
}
