package io.github.krabek515.canopy.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class WornClothing {
    private WornClothing() {
    }

    public static float heat(Player player) {
        return sum(player, ClothingItem::heat);
    }

    public static float bite(Player player) {
        return sum(player, ClothingItem::bite);
    }

    public static float wetGain(Player player) {
        float gain = 1f + sum(player, ClothingItem::wetGain);
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof ClothingItem && RubberSapItem.waxed(stack)) {
                gain -= 0.18f;
            }
        }
        return Math.max(0.25f, gain);
    }

    public static float drySpeed(Player player) {
        return Math.max(0.3f, 1f + sum(player, ClothingItem::drySpeed));
    }

    public static float noise(Player player) {
        return sum(player, ClothingItem::noise);
    }

    public static float sprintTax(Player player) {
        float tax = 0f;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof ClothingItem clothing && clothing.heavy()) {
                tax += 0.008f;
            }
        }
        return tax;
    }

    private static float sum(Player player, java.util.function.ToDoubleFunction<ClothingItem> value) {
        float total = 0f;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.getItem() instanceof ClothingItem clothing) {
                total += (float) value.applyAsDouble(clothing);
            }
        }
        return total;
    }
}
