package io.github.krabek515.canopy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HintedItem extends Item {
    private final String hint;

    public HintedItem(Properties properties, String hint) {
        super(properties);
        this.hint = hint;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(hint).withStyle(ChatFormatting.GRAY));
    }
}
