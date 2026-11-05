package io.github.krabek515.canopy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Каучуковый сок с кроны. В сетке с одеждой даёт пропитку: вещь почти не мокнет. */
public class RubberSapItem extends Item {
    public static final String TAG_WAXED = "Waxed";

    public RubberSapItem(Properties properties) {
        super(properties);
    }

    public static boolean waxed(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(TAG_WAXED);
    }

    public static void wax(ItemStack stack) {
        stack.getOrCreateTag().putBoolean(TAG_WAXED, true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.canopy.rubber_sap.hint").withStyle(ChatFormatting.GRAY));
    }
}
