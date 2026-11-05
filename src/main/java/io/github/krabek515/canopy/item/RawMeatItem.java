package io.github.krabek515.canopy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RawMeatItem extends Item {
    public static final String TAG_SPOIL = "Spoil";
    public static final int SPOIL_SECONDS = 600;

    public RawMeatItem(Properties properties) {
        super(properties);
    }

    public static int spoilOf(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(TAG_SPOIL) : 0;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (CampfireCooking.isLit(context.getLevel().getBlockState(context.getClickedPos()))) {
            return CampfireCooking.cook(context, CanopyItems.COOKED_MEAT.get());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int spoil = spoilOf(stack);
        String key = spoil >= SPOIL_SECONDS * 3 / 4
                ? "item.canopy.raw_meat.spoiled_soon"
                : spoil > 0 ? "item.canopy.raw_meat.spoiling" : "item.canopy.raw_meat.hint";
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }
}
