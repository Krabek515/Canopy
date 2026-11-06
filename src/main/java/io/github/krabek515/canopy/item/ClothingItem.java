package io.github.krabek515.canopy.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClothingItem extends DyeableArmorItem {
    private static final Map<EquipmentSlot, UUID> SPEED = Map.of(
            EquipmentSlot.HEAD, UUID.fromString("6b1e2a10-1f4a-4c3e-9a11-0a0b1c2d3e01"),
            EquipmentSlot.CHEST, UUID.fromString("6b1e2a10-1f4a-4c3e-9a11-0a0b1c2d3e02"),
            EquipmentSlot.LEGS, UUID.fromString("6b1e2a10-1f4a-4c3e-9a11-0a0b1c2d3e03"),
            EquipmentSlot.FEET, UUID.fromString("6b1e2a10-1f4a-4c3e-9a11-0a0b1c2d3e04")
    );

    private final int color;
    private final float heat;
    private final float bite;
    private final float wetGain;
    private final float drySpeed;
    private final float noise;
    private final boolean heavy;
    private final String hint;

    public ClothingItem(ArmorMaterial material, ArmorItem.Type type, int color, float heat, float bite, float wetGain, float drySpeed, float noise, boolean heavy, String hint, Item.Properties properties) {
        super(material, type, properties);
        this.color = color;
        this.heat = heat;
        this.bite = bite;
        this.wetGain = wetGain;
        this.drySpeed = drySpeed;
        this.noise = noise;
        this.heavy = heavy;
        this.hint = hint;
    }

    public float heat() {
        return this.heat;
    }

    public float bite() {
        return this.bite;
    }

    public float wetGain() {
        return this.wetGain;
    }

    public float drySpeed() {
        return this.drySpeed;
    }

    public float noise() {
        return this.noise;
    }

    public boolean heavy() {
        return this.heavy;
    }

    @Override
    public int getColor(ItemStack stack) {
        return this.color;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "canopy:textures/models/armor/" + this.material.getName() + "_layer_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.hint).withStyle(ChatFormatting.GRAY));
        if (RubberSapItem.waxed(stack)) {
            tooltip.add(Component.translatable("item.canopy.waxed").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> modifiers = super.getDefaultAttributeModifiers(slot);
        if (!this.heavy || slot != this.getType().getSlot()) {
            return modifiers;
        }
        Multimap<Attribute, AttributeModifier> copy = ArrayListMultimap.create(modifiers);
        copy.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED.get(slot), "canopy_hide_weight", -0.008, AttributeModifier.Operation.ADDITION));
        return copy;
    }
}
