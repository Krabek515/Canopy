package io.github.krabek515.canopy.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RespiratorItem extends DyeableArmorItem {
    public RespiratorItem(Properties properties) {
        super(RespiratorMaterial.FILTER, ArmorItem.Type.HELMET, properties);
    }

    @Override
    public int getColor(ItemStack stack) {
        return 0x4A4E46;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "canopy:textures/models/armor/respirator_layer_1.png";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.canopy.respirator.hint"));
    }
}
