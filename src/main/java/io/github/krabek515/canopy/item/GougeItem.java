package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.body.HandWork;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GougeItem extends DiggerItem {
    public GougeItem(Properties properties) {
        super(1f, -2.8f, Tiers.STONE, HandWork.VEIN, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.canopy.stone_gouge.hint"));
    }
}
