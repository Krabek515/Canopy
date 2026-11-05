package io.github.krabek515.canopy.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ClothingMaterial implements ArmorMaterial {
    FIBER(5, new int[]{1, 2, 3, 1}, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.BAMBOO)),
    HIDE(15, new int[]{2, 5, 6, 2}, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(CanopyItems.HIDE.get()));

    private static final int[] DURABILITY = {13, 15, 16, 11};
    private final int factor;
    private final int[] defense;
    private final SoundEvent sound;
    private final Supplier<Ingredient> repair;

    ClothingMaterial(int factor, int[] defense, SoundEvent sound, Supplier<Ingredient> repair) {
        this.factor = factor;
        this.defense = defense;
        this.sound = sound;
        this.repair = repair;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.getSlot().getIndex()] * this.factor;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.defense[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repair.get();
    }

    @Override
    public String getName() {
        return "leather";
    }

    @Override
    public float getToughness() {
        return 0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0f;
    }
}
