package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.item.FlaskItem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ClientModEvents {
    private ClientModEvents() {
    }

    @Mod.EventBusSubscriber(modid = Canopy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModBus {
        private ModBus() {
        }

        @SubscribeEvent
        public static void overlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "vitals", VitalsHud.OVERLAY);
        }

        @SubscribeEvent
        public static void setup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> net.minecraft.client.renderer.item.ItemProperties.register(
                    CanopyItems.FLASK.get(),
                    ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "water"),
                    (stack, level, entity, seed) -> FlaskItem.waterOf(stack).modelValue()
            ));
        }
    }

    @Mod.EventBusSubscriber(modid = Canopy.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeBus {
        private ForgeBus() {
        }

        @SubscribeEvent
        public static void stopSprint(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.player != Minecraft.getInstance().player) {
                return;
            }
            event.player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
                if (stats.stamina() <= 0f) {
                    event.player.setSprinting(false);
                }
            });
        }
    }
}
