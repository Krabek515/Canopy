package io.github.krabek515.canopy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TaroRootItem extends BlockItem {
    public TaroRootItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (CampfireCooking.isLit(context.getLevel().getBlockState(context.getClickedPos()))) {
            return CampfireCooking.cook(context, CanopyItems.COOKED_TARO.get());
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.canopy.taro_root.hint").withStyle(ChatFormatting.GRAY));
    }
}
