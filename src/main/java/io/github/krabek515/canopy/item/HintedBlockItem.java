package io.github.krabek515.canopy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HintedBlockItem extends BlockItem {
    private final String hint;

    public HintedBlockItem(Block block, Properties properties, String hint) {
        super(block, properties);
        this.hint = hint;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(hint).withStyle(ChatFormatting.GRAY));
    }
}
