package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.body.CanopyEffects;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.network.CanopyNetwork;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DoseItem extends HintedItem {
    private final boolean antidote;

    public DoseItem(Properties properties, boolean antidote, String hint) {
        super(properties.stacksTo(16), hint);
        this.antidote = antidote;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            if (antidote) {
                boolean venom = player.hasEffect(CanopyEffects.VENOM.get());
                player.removeEffect(CanopyEffects.VENOM.get());
                player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> stats.setTemperature(stats.temperature() - 4f));
                player.displayClientMessage(Component.translatable(venom ? "message.canopy.antidote" : "message.canopy.antidote_spare"), true);
                if (venom) {
                    CanopyAdvancements.award(player, "antidote");
                }
                JournalLearning.note(player, Journal.ANTIDOTE);
            } else {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 0));
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0));
                player.displayClientMessage(Component.translatable("message.canopy.bad_brew"), true);
                JournalLearning.note(player, Journal.BAD_BREW);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.55f, antidote ? 1.1f : 0.8f);
            CanopyNetwork.send(player);
        }
        if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return stack;
    }
}
