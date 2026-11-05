package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CanopyRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Canopy.MOD_ID);
    public static final RegistryObject<RecipeSerializer<WaxRecipe>> WAXING = SERIALIZERS.register("waxing", () -> WaxRecipe.SERIALIZER);

    private CanopyRecipes() {
    }
}
