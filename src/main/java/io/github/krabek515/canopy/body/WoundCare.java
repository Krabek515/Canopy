package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.config.CanopyConfig;
import io.github.krabek515.canopy.item.FlaskWater;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.network.CanopyNetwork;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
public final class WoundCare {
    public static final UUID FEVER_HEALTH = UUID.fromString("6c1a0a1e-7b2e-4c3a-9d11-2a0e5b7c8d91");

    private WoundCare() {
    }

    public static void tick(ServerPlayer player, SurvivalStats stats) {
        long now = player.level().getGameTime();
        long limit = Math.round(CanopyConfig.WOUND_MINUTES.get() * 60.0 * 20.0);
        if (stats.wound() == SurvivalStats.WOUND_OPEN && stats.woundDirty() && now - stats.woundClock() >= limit) {
            stats.assignWound(SurvivalStats.WOUND_INFECTED, true, now);
            player.displayClientMessage(Component.translatable("message.canopy.infected"), true);
        } else if (stats.wound() == SurvivalStats.WOUND_INFECTED && now - stats.woundClock() >= limit) {
            stats.assignWound(SurvivalStats.WOUND_FEVER, stats.woundDirty(), now);
            player.displayClientMessage(Component.translatable("message.canopy.fever"), true);
        }
        applyEffects(player, stats);
    }

    public static void open(ServerPlayer player) {
        long now = player.level().getGameTime();
        boolean[] changed = {false};
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            if (stats.wound() == SurvivalStats.WOUND_NONE) {
                stats.assignWound(SurvivalStats.WOUND_OPEN, true, now);
                stats.startBleeding(now);
                player.displayClientMessage(Component.translatable("message.canopy.wound_open"), true);
                changed[0] = true;
            } else if (!stats.woundDirty()) {
                boolean open = stats.wound() == SurvivalStats.WOUND_OPEN;
                stats.assignWound(open ? SurvivalStats.WOUND_OPEN : stats.wound(), true, open ? now : stats.woundClock());
                player.displayClientMessage(Component.translatable("message.canopy.wound_open"), true);
                changed[0] = true;
            }
        });
        if (changed[0]) {
            CanopyNetwork.send(player);
        }
    }

    public static boolean wash(ServerPlayer player, FlaskWater water) {
        boolean[] consumed = {false};
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            if (water == FlaskWater.CLEAN) {
                if (stats.wound() == SurvivalStats.WOUND_NONE || !stats.woundDirty()) {
                    player.displayClientMessage(Component.translatable("message.canopy.wash_none"), true);
                    return;
                }
                boolean late = stats.wound() >= SurvivalStats.WOUND_INFECTED;
                stats.setWoundDirty(false);
                player.displayClientMessage(Component.translatable(late ? "message.canopy.wash_late" : "message.canopy.washed"), true);
                consumed[0] = true;
                return;
            }
            if (stats.wound() == SurvivalStats.WOUND_NONE) {
                player.displayClientMessage(Component.translatable("message.canopy.wash_none"), true);
                return;
            }
            if (stats.wound() == SurvivalStats.WOUND_OPEN) {
                stats.assignWound(SurvivalStats.WOUND_OPEN, true, player.level().getGameTime());
            } else {
                stats.setWoundDirty(true);
            }
            player.displayClientMessage(Component.translatable("message.canopy.murky_wound"), true);
            consumed[0] = true;
        });
        if (consumed[0]) {
            CanopyNetwork.send(player);
        }
        return consumed[0];
    }

    public static boolean bandage(ServerPlayer player) {
        boolean[] used = {false};
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            boolean wounded = stats.wound() != SurvivalStats.WOUND_NONE;
            if (!wounded) {
                player.displayClientMessage(Component.translatable("message.canopy.bandage_nothing"), true);
                return;
            }
            if (stats.woundDirty()) {
                player.displayClientMessage(Component.translatable("message.canopy.bandage_dirty"), true);
                return;
            }
            if (wounded && !stats.woundDirty()) {
                stats.assignWound(SurvivalStats.WOUND_NONE, false, player.level().getGameTime());
                stats.setBleedingUntil(0L);
                clearFever(player);
                player.displayClientMessage(Component.translatable("message.canopy.bandage_close"), true);
                CanopyAdvancements.award(player, "bound");
                used[0] = true;
                JournalLearning.note(player, Journal.CRAFT_BANDAGE);
            }
        });
        if (used[0]) {
            CanopyNetwork.send(player);
        }
        return used[0];
    }

    public static void applyEffects(ServerPlayer player, SurvivalStats stats) {
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health == null) {
            return;
        }
        if (stats.wound() == SurvivalStats.WOUND_FEVER) {
            if (health.getModifier(FEVER_HEALTH) == null) {
                health.addTransientModifier(new AttributeModifier(FEVER_HEALTH, "canopy_fever", -6.0, AttributeModifier.Operation.ADDITION));
                player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
            }
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0, false, false, true));
        } else if (health.getModifier(FEVER_HEALTH) != null) {
            health.removeModifier(FEVER_HEALTH);
        }
        if (stats.wound() == SurvivalStats.WOUND_INFECTED) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false, true));
        }
    }

    public static void clearFever(ServerPlayer player) {
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health != null && health.getModifier(FEVER_HEALTH) != null) {
            health.removeModifier(FEVER_HEALTH);
        }
        player.removeEffect(MobEffects.CONFUSION);
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            if (stats.wound() == SurvivalStats.WOUND_NONE) {
                return;
            }
            float cap = player.getMaxHealth() * (stats.wound() == SurvivalStats.WOUND_FEVER ? 0.45f : 0.65f);
            float room = cap - player.getHealth();
            if (room <= 0.05f) {
                event.setCanceled(true);
                return;
            }
            event.setAmount(Math.min(event.getAmount(), room));
        });
    }
}
