package io.github.krabek515.canopy.item;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

/** Одежда плюс каучуковый сок в любой клетке: та же вещь с пропиткой. Повторно не пропитывается. */
public class WaxRecipe extends CustomRecipe {
    public static final RecipeSerializer<WaxRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(WaxRecipe::new);

    public WaxRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return find(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ItemStack clothing = find(container);
        if (clothing == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = clothing.copyWithCount(1);
        RubberSapItem.wax(result);
        return result;
    }

    private static ItemStack find(CraftingContainer container) {
        ItemStack clothing = null;
        boolean sap = false;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof ClothingItem && clothing == null && !RubberSapItem.waxed(stack)) {
                clothing = stack;
            } else if (stack.is(CanopyItems.RUBBER_SAP.get()) && !sap) {
                sap = true;
            } else {
                return null;
            }
        }
        return clothing != null && sap ? clothing : null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
