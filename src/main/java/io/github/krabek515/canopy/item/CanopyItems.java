package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.block.CanopyBlocks;
import io.github.krabek515.canopy.entity.CanopyEntities;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CanopyItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Canopy.MOD_ID);
    public static final RegistryObject<Item> FLASK = ITEMS.register("bamboo_flask", () -> new FlaskItem(new Item.Properties()));
    public static final RegistryObject<Item> DIARY = ITEMS.register("journal", () -> new DiaryItem(new Item.Properties()));
    public static final RegistryObject<Item> STONE_MACHETE = ITEMS.register("stone_machete", () -> new MacheteItem(net.minecraft.world.item.Tiers.STONE, 3, "item.canopy.stone_machete.hint", false, new Item.Properties()));
    public static final RegistryObject<Item> METAL_MACHETE = ITEMS.register("metal_machete", () -> new MacheteItem(net.minecraft.world.item.Tiers.IRON, 2, "item.canopy.metal_machete.hint", true, new Item.Properties()));
    public static final RegistryObject<Item> GOUGE = ITEMS.register("stone_gouge", () -> new GougeItem(new Item.Properties()));
    public static final RegistryObject<Item> NUGGET = ITEMS.register("nugget", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EXPOSED_VEIN = ITEMS.register("exposed_vein", () -> new BlockItem(CanopyBlocks.EXPOSED_VEIN.get(), new Item.Properties()));
    public static final RegistryObject<Item> EXPEDITION_CACHE = ITEMS.register("expedition_cache", () -> new BlockItem(CanopyBlocks.EXPEDITION_CACHE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RESPIRATOR = ITEMS.register("respirator", () -> new RespiratorItem(new Item.Properties()));
    public static final RegistryObject<Item> GAS_SEEP = ITEMS.register("gas_seep", () -> new BlockItem(CanopyBlocks.GAS_SEEP.get(), new Item.Properties()));
    public static final RegistryObject<Item> AIR_PLANT = ITEMS.register("air_plant", () -> new BlockItem(CanopyBlocks.AIR_PLANT.get(), new Item.Properties()));
    public static final RegistryObject<Item> TARO_ROOT = ITEMS.register("taro_root", () -> new TaroRootItem(CanopyBlocks.TARO.get(), new Item.Properties().food(
            new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationMod(0.05f)
                    .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300, 0), 1f)
                    .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 160, 0), 1f)
                    .build()
    )));
    public static final RegistryObject<Item> COOKED_TARO = ITEMS.register("cooked_taro", () -> new HintedItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(4).saturationMod(0.25f).build()
    ), "item.canopy.cooked_taro.hint"));
    public static final RegistryObject<Item> RAW_MEAT = ITEMS.register("raw_meat", () -> new RawMeatItem(new Item.Properties().food(
            new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationMod(0.05f)
                    .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 400, 0), 1f)
                    .build()
    )));
    public static final RegistryObject<Item> COOKED_MEAT = ITEMS.register("cooked_meat", () -> new HintedItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(6).saturationMod(0.35f).build()
    ), "item.canopy.cooked_meat.hint"));
    public static final RegistryObject<Item> SPOILED_MEAT = ITEMS.register("spoiled_meat", () -> new HintedItem(new Item.Properties().food(
            new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationMod(0.02f)
                    .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300, 0), 1f)
                    .effect(() -> new MobEffectInstance(MobEffects.POISON, 160, 0), 1f)
                    .build()
    ), "item.canopy.spoiled_meat.hint"));
    public static final RegistryObject<Item> BANDAGE = ITEMS.register("bandage", () -> new BandageItem(new Item.Properties()));
    public static final RegistryObject<Item> BITTER_HERB = ITEMS.register("bitter_herb", () -> new BitterHerbItem(CanopyBlocks.BITTER_HERB.get(), new Item.Properties()));
    public static final RegistryObject<Item> BAD_BREW = ITEMS.register("bad_brew", () -> new DoseItem(new Item.Properties(), false, "item.canopy.bad_brew.hint"));
    public static final RegistryObject<Item> ANTIDOTE = ITEMS.register("antidote", () -> new DoseItem(new Item.Properties(), true, "item.canopy.antidote.hint"));
    public static final RegistryObject<Item> THORNY_BRUSH = ITEMS.register("thorny_brush", () -> new BlockItem(CanopyBlocks.THORNY_BRUSH.get(), new Item.Properties()));
    public static final RegistryObject<Item> ROTTEN_DECK = ITEMS.register("rotten_deck", () -> new BlockItem(CanopyBlocks.ROTTEN_DECK.get(), new Item.Properties()));
    public static final RegistryObject<Item> MARKED_CARGO = ITEMS.register("marked_cargo", () -> new BlockItem(CanopyBlocks.MARKED_CARGO.get(), new Item.Properties()));
    public static final RegistryObject<Item> SILK = ITEMS.register("silk", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SILK_POD = ITEMS.register("silk_pod", () -> new BlockItem(CanopyBlocks.SILK_POD.get(), new Item.Properties()));
    public static final RegistryObject<Item> ROPE = ITEMS.register("rope", () -> new BlockItem(CanopyBlocks.ROPE.get(), new Item.Properties()));
    public static final RegistryObject<Item> HIDE = ITEMS.register("hide", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HELICONIA = ITEMS.register("heliconia", () -> new BlockItem(CanopyBlocks.HELICONIA.get(), new Item.Properties()));
    public static final RegistryObject<Item> GINGER_LILY = ITEMS.register("ginger_lily", () -> new BlockItem(CanopyBlocks.GINGER_LILY.get(), new Item.Properties()));
    public static final RegistryObject<Item> BROMELIAD = ITEMS.register("bromeliad", () -> new BlockItem(CanopyBlocks.BROMELIAD.get(), new Item.Properties()));
    public static final RegistryObject<Item> LOG_FUNGUS = ITEMS.register("log_fungus", () -> new HintedBlockItem(CanopyBlocks.LOG_FUNGUS.get(), new Item.Properties().food(
            new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).build()
    ), "item.canopy.log_fungus.hint"));
    public static final RegistryObject<Item> ORCHID = ITEMS.register("orchid", () -> new BlockItem(CanopyBlocks.ORCHID.get(), new Item.Properties()));
    public static final RegistryObject<Item> BANANA = ITEMS.register("banana", () -> new HintedItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()
    ), "item.canopy.banana.hint"));
    public static final RegistryObject<Item> BANANA_BUNCH = ITEMS.register("banana_bunch", () -> new BlockItem(CanopyBlocks.BANANA_BUNCH.get(), new Item.Properties()));
    public static final RegistryObject<Item> WATER_LIANA = ITEMS.register("water_liana", () -> new BlockItem(CanopyBlocks.WATER_LIANA.get(), new Item.Properties()));
    public static final RegistryObject<Item> FIBER_HOOD = clothing("fiber_hood", ClothingMaterial.FIBER, ArmorItem.Type.HELMET, 0x6E8B54, -1.5f, 0.05f, -0.12f, 0.25f, 0f, false, "item.canopy.fiber.hint");
    public static final RegistryObject<Item> FIBER_TUNIC = clothing("fiber_tunic", ClothingMaterial.FIBER, ArmorItem.Type.CHESTPLATE, 0x6E8B54, -1.5f, 0.05f, -0.12f, 0.25f, 0f, false, "item.canopy.fiber.hint");
    public static final RegistryObject<Item> FIBER_LEGGINGS = clothing("fiber_leggings", ClothingMaterial.FIBER, ArmorItem.Type.LEGGINGS, 0x6E8B54, -1.5f, 0.05f, -0.12f, 0.25f, 0f, false, "item.canopy.fiber.hint");
    public static final RegistryObject<Item> FIBER_SANDALS = clothing("fiber_sandals", ClothingMaterial.FIBER, ArmorItem.Type.BOOTS, 0x6E8B54, -1.5f, 0.05f, -0.12f, 0.25f, 0f, false, "item.canopy.fiber.hint");
    public static final RegistryObject<Item> HIDE_HOOD = clothing("hide_hood", ClothingMaterial.HIDE, ArmorItem.Type.HELMET, 0x6B4423, 3f, 0.18f, 0.08f, -0.15f, 0.15f, true, "item.canopy.hide.hint");
    public static final RegistryObject<Item> HIDE_COAT = clothing("hide_coat", ClothingMaterial.HIDE, ArmorItem.Type.CHESTPLATE, 0x6B4423, 3f, 0.18f, 0.08f, -0.15f, 0.15f, true, "item.canopy.hide.hint");
    public static final RegistryObject<Item> HIDE_LEGGINGS = clothing("hide_leggings", ClothingMaterial.HIDE, ArmorItem.Type.LEGGINGS, 0x6B4423, 3f, 0.18f, 0.08f, -0.15f, 0.15f, true, "item.canopy.hide.hint");
    public static final RegistryObject<Item> HIDE_BOOTS = clothing("hide_boots", ClothingMaterial.HIDE, ArmorItem.Type.BOOTS, 0x6B4423, 3f, 0.18f, 0.08f, -0.15f, 0.15f, true, "item.canopy.hide.hint");
    public static final RegistryObject<Item> JAGUAR_SPAWN_EGG = ITEMS.register("jaguar_spawn_egg", () -> new ForgeSpawnEggItem(CanopyEntities.JAGUAR, 0xC47A2A, 0x1C1A17, new Item.Properties()));
    public static final RegistryObject<Item> AGOUTI_SPAWN_EGG = ITEMS.register("agouti_spawn_egg", () -> new ForgeSpawnEggItem(CanopyEntities.AGOUTI, 0x8A6239, 0xE6D2A8, new Item.Properties()));
    public static final RegistryObject<Item> SNAKE_SPAWN_EGG = ITEMS.register("snake_spawn_egg", () -> new ForgeSpawnEggItem(CanopyEntities.SNAKE, 0x3E6B32, 0x1E2A16, new Item.Properties()));
    public static final RegistryObject<Item> MONKEY_SPAWN_EGG = ITEMS.register("monkey_spawn_egg", () -> new ForgeSpawnEggItem(CanopyEntities.MONKEY, 0x6B4A2A, 0xD7C4A3, new Item.Properties()));
    public static final RegistryObject<Item> CAPYBARA_SPAWN_EGG = ITEMS.register("capybara_spawn_egg", () -> new ForgeSpawnEggItem(CanopyEntities.CAPYBARA, 0x8C6A3E, 0x3C2A18, new Item.Properties()));
    public static final RegistryObject<Item> TAPIR_SPAWN_EGG = ITEMS.register("tapir_spawn_egg", () -> new ForgeSpawnEggItem(CanopyEntities.TAPIR, 0x3B2F28, 0x8E8378, new Item.Properties()));
    public static final RegistryObject<Item> CAIMAN_SPAWN_EGG = ITEMS.register("caiman_spawn_egg", () -> new ForgeSpawnEggItem(CanopyEntities.CAIMAN, 0x3F4A2A, 0xC9C48A, new Item.Properties()));
    public static final RegistryObject<Item> GORILLA_SPAWN_EGG = ITEMS.register("gorilla_spawn_egg", () -> new ForgeSpawnEggItem(CanopyEntities.GORILLA, 0x1E1A18, 0x6E6A66, new Item.Properties()));
    public static final RegistryObject<Item> TOUCAN_SPAWN_EGG = ITEMS.register("toucan_spawn_egg", () -> new ForgeSpawnEggItem(CanopyEntities.TOUCAN, 0x16140F, 0xF0A020, new Item.Properties()));
    public static final RegistryObject<Item> FEATHER_TUFT = ITEMS.register("feather_tuft", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BONE_SHARD = ITEMS.register("bone_shard", () -> new HintedItem(new Item.Properties(), "item.canopy.bone_shard.hint"));
    public static final RegistryObject<Item> BONE_MACHETE = ITEMS.register("bone_machete", () -> new MacheteItem(net.minecraft.world.item.Tiers.IRON, 2, "item.canopy.bone_machete.hint", false, new Item.Properties()));
    public static final RegistryObject<Item> RUBBER_SAP = ITEMS.register("rubber_sap", () -> new RubberSapItem(new Item.Properties()));
    public static final RegistryObject<Item> RUBBER_KNOT = ITEMS.register("rubber_knot", () -> new BlockItem(CanopyBlocks.RUBBER_KNOT.get(), new Item.Properties()));
    public static final RegistryObject<Item> CLAY_OVEN = ITEMS.register("clay_oven", () -> new BlockItem(CanopyBlocks.CLAY_OVEN.get(), new Item.Properties()));
    public static final RegistryObject<Item> STILT_PLATFORM = ITEMS.register("stilt_platform", () -> new HintedBlockItem(CanopyBlocks.STILT_PLATFORM.get(), new Item.Properties(), "item.canopy.stilt_platform.hint"));
    public static final RegistryObject<Item> HAMMOCK = ITEMS.register("hammock", () -> new HintedBlockItem(CanopyBlocks.HAMMOCK.get(), new Item.Properties(), "item.canopy.hammock.hint"));
    public static final RegistryObject<Item> STEW = ITEMS.register("stew", () -> new HintedItem(new Item.Properties().stacksTo(1).food(
            new FoodProperties.Builder().nutrition(10).saturationMod(0.8f).alwaysEat().build()
    ), "item.canopy.stew.hint"));

    private static RegistryObject<Item> clothing(String name, ClothingMaterial material, ArmorItem.Type type, int color, float heat, float bite, float wetGain, float drySpeed, float noise, boolean heavy, String hint) {
        return ITEMS.register(name, () -> new ClothingItem(material, type, color, heat, bite, wetGain, drySpeed, noise, heavy, hint, new Item.Properties()));
    }

    private CanopyItems() {
    }
}
