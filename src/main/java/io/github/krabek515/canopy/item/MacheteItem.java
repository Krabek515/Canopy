package io.github.krabek515.canopy.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MacheteItem extends SwordItem {
    private final String hint;
    private final boolean metal;

    public MacheteItem(Tier tier, int damage, String hint, boolean metal, Properties properties) {
        super(tier, damage, -2.7f, properties);
        this.hint = hint;
        this.metal = metal;
    }

    public boolean metal() {
        return this.metal;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.hint));
    }
}
