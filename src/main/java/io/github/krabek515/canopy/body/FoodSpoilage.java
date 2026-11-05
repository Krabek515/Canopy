package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.item.RawMeatItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class FoodSpoilage {
    private FoodSpoilage() {
    }

    public static void tickSecond(ServerPlayer player, SurvivalStats stats) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (stats.wetness() <= 0.45f || stats.nearHeat()) {
            return;
        }
        boolean spoiled = spoil(player.getInventory().items);
        spoiled |= spoil(player.getInventory().offhand);
        if (spoiled) {
            player.displayClientMessage(Component.translatable("message.canopy.spoiled"), true);
        }
    }

    private static boolean spoil(List<ItemStack> stacks) {
        boolean changed = false;
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (!stack.is(CanopyItems.RAW_MEAT.get()) || stack.isEmpty()) {
                continue;
            }
            int spoil = RawMeatItem.spoilOf(stack) + 1;
            if (spoil >= RawMeatItem.SPOIL_SECONDS) {
                stacks.set(i, new ItemStack(CanopyItems.SPOILED_MEAT.get(), stack.getCount()));
                changed = true;
            } else {
                stack.getOrCreateTag().putInt(RawMeatItem.TAG_SPOIL, spoil);
            }
        }
        return changed;
    }
}
