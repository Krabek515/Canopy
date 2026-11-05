package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.body.WoundCare;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BandageItem extends HintedItem {
    public BandageItem(Properties properties) {
        super(properties.stacksTo(16), "item.canopy.bandage.hint");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        if (!(player instanceof ServerPlayer server) || !WoundCare.bandage(server)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }
}
