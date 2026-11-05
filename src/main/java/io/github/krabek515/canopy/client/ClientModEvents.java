package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.entity.CanopyEntities;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.item.FlaskItem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
        public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(CanopyEntities.JAGUAR.get(), ctx -> new BeastRenderer<>(ctx, "jaguar", 0.6f));
            event.registerEntityRenderer(CanopyEntities.AGOUTI.get(), ctx -> new BeastRenderer<>(ctx, "agouti", 0.3f));
            event.registerEntityRenderer(CanopyEntities.SNAKE.get(), ctx -> new BeastRenderer<>(ctx, "snake", 0.35f));
            event.registerEntityRenderer(CanopyEntities.MONKEY.get(), ctx -> new BeastRenderer<>(ctx, "monkey", 0.35f));
            event.registerEntityRenderer(CanopyEntities.CAPYBARA.get(), ctx -> new BeastRenderer<>(ctx, "capybara", 0.6f));
            event.registerEntityRenderer(CanopyEntities.TAPIR.get(), ctx -> new BeastRenderer<>(ctx, "tapir", 0.7f));
            event.registerEntityRenderer(CanopyEntities.CAIMAN.get(), ctx -> new BeastRenderer<>(ctx, "caiman", 0.7f));
            event.registerEntityRenderer(CanopyEntities.GORILLA.get(), ctx -> new BeastRenderer<>(ctx, "gorilla", 0.8f));
            event.registerEntityRenderer(CanopyEntities.TOUCAN.get(), ctx -> new BeastRenderer<>(ctx, "toucan", 0.3f));
        }

        @SubscribeEvent
        public static void setup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                net.minecraft.client.renderer.item.ItemProperties.register(
                        CanopyItems.FLASK.get(),
                        ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "water"),
                        (stack, level, entity, seed) -> FlaskItem.waterOf(stack).modelValue()
                );
                Minecraft.getInstance().getItemColors().register(
                        (stack, index) -> index > 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).getColor(stack),
                        CanopyItems.FIBER_HOOD.get(),
                        CanopyItems.FIBER_TUNIC.get(),
                        CanopyItems.FIBER_LEGGINGS.get(),
                        CanopyItems.FIBER_SANDALS.get(),
                        CanopyItems.HIDE_HOOD.get(),
                        CanopyItems.HIDE_COAT.get(),
                        CanopyItems.HIDE_LEGGINGS.get(),
                        CanopyItems.HIDE_BOOTS.get()
                );
            });
        }
    }

    @Mod.EventBusSubscriber(modid = Canopy.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeBus {
        private ForgeBus() {
        }

    }
}
