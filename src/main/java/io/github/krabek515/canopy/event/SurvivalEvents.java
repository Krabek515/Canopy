package io.github.krabek515.canopy.event;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.body.Blackout;
import io.github.krabek515.canopy.body.BodySimulation;
import io.github.krabek515.canopy.body.HeatSources;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.config.CanopyConfig;
import io.github.krabek515.canopy.network.CanopyNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
public final class SurvivalEvents {
    private SurvivalEvents() {
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            if (player.tickCount % 20 == 0) {
                stats.setNearHeat(HeatSources.near(player, 5));
                BodySimulation.tickSecond(player, stats);
            }
            BodySimulation.tickStamina(player, stats);
            if (player.tickCount % 5 == 0 && stats.isDirty()) {
                stats.clearDirty();
                CanopyNetwork.send(player);
            }
        });
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            float speed = event.getNewSpeed();
            if (stats.stamina() <= 0.5f) {
                speed *= 0.45f;
            }
            if (stats.temperature() < 36f || stats.temperature() > 64f) {
                speed *= 0.75f;
            }
            if (stats.wetness() > 0.8f) {
                speed *= 0.85f;
            }
            event.setNewSpeed(speed);
        });
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || Blackout.unavoidable(event.getSource())) {
            return;
        }
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            if (stats.isRecovering(player.level().getGameTime())) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || Blackout.unavoidable(event.getSource())) {
            return;
        }
        if (!CanopyConfig.BLACKOUT.get()) {
            return;
        }
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            event.setCanceled(true);
            Blackout.wake(player, stats);
        });
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(CanopyCapabilities.SURVIVAL).ifPresent(oldStats ->
                event.getEntity().getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> stats.copyFrom(oldStats)));
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CanopyNetwork.send(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CanopyNetwork.send(player);
        }
    }

    @SubscribeEvent
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CanopyNetwork.send(player);
        }
    }
}
