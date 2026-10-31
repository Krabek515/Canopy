package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CanopyItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Canopy.MOD_ID);
    public static final RegistryObject<Item> FLASK = ITEMS.register("bamboo_flask", () -> new FlaskItem(new Item.Properties()));

    private CanopyItems() {
    }
}
