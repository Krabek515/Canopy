package io.github.krabek515.canopy.entity;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.body.CanopyEffects;
import io.github.krabek515.canopy.body.WoundCare;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.item.WornClothing;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
public final class BeastEvents {
    private static final String VENOM_MEMORY = "CanopyVenom";

    private BeastEvents() {
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        Entity attacker = event.getSource().getEntity();
        boolean jaguar = attacker instanceof Jaguar;
        boolean snake = attacker instanceof Snake;
        boolean caiman = attacker instanceof Caiman;
        boolean gorilla = attacker instanceof Gorilla;
        if (!jaguar && !snake && !caiman && !gorilla) {
            return;
        }
        float protection = Math.min(0.75f, WornClothing.bite(player));
        float base = jaguar ? 5f : caiman ? 6f : gorilla ? 8f : 2f;
        event.setAmount(base * (1f - protection));
        if (gorilla) {
            JournalLearning.note(player, Journal.GORILLA);
            JournalLearning.note(player, Journal.GORILLA_HIT);
            return;
        }
        if (caiman) {
            JournalLearning.note(player, Journal.CAIMAN);
            JournalLearning.note(player, Journal.CAIMAN_BITE);
            WoundCare.open(player);
            return;
        }
        JournalLearning.note(player, jaguar ? Journal.JAGUAR : Journal.SNAKE);
        JournalLearning.note(player, jaguar ? Journal.JAGUAR_BITE : Journal.SNAKE_BITE);
        if (jaguar) {
            WoundCare.open(player);
            return;
        }
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            if (stats.startBleeding(player.level().getGameTime())) {
                player.displayClientMessage(Component.translatable("message.canopy.bleeding"), true);
            }
        });
    }

    @SubscribeEvent
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || !event.getItem().is(Items.MILK_BUCKET)) {
            return;
        }
        MobEffectInstance venom = entity.getEffect(CanopyEffects.VENOM.get());
        if (venom != null) {
            entity.getPersistentData().putInt(VENOM_MEMORY, venom.getDuration());
        }
    }

    @SubscribeEvent
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) {
        event.getEntity().getPersistentData().remove(VENOM_MEMORY);
    }

    @SubscribeEvent
    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || !event.getItem().is(Items.MILK_BUCKET)) {
            return;
        }
        int left = entity.getPersistentData().getInt(VENOM_MEMORY);
        entity.getPersistentData().remove(VENOM_MEMORY);
        if (left <= 0) {
            return;
        }
        entity.addEffect(new MobEffectInstance(CanopyEffects.VENOM.get(), left, 0));
        if (entity instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("message.canopy.milk"), true);
        }
    }

    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getCrafting().is(CanopyItems.FIBER_TUNIC.get())) {
            CanopyAdvancements.award(player, "woven");
        }
    }
}
