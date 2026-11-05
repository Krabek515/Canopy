package io.github.krabek515.canopy.body;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
public final class PlainMeals {
    public static final TagKey<Item> MEALS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "meals"));
    public static final int VANILLA_HUNGER_CAP = 2;

    private PlainMeals() {
    }

    @SubscribeEvent
    public static void onEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack eaten = event.getItem();
        if (eaten.is(MEALS)) {
            return;
        }
        FoodProperties food = eaten.getFoodProperties(player);
        if (food == null) {
            return;
        }
        int nutrition = food.getNutrition();
        int allowed = Math.min(VANILLA_HUNGER_CAP, nutrition);
        FoodData data = player.getFoodData();
        data.setFoodLevel(Math.max(0, data.getFoodLevel() - (nutrition - allowed)));
        float addedSat = nutrition * food.getSaturationModifier() * 2f;
        float keepSat = allowed * 0.15f * 2f;
        data.setSaturation(Mth.clamp(data.getSaturationLevel() - addedSat + keepSat, 0f, data.getFoodLevel()));
    }
}
