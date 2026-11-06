package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.item.CanopyItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Узлы древа дневника: карта мода. Узел виден всегда — известный яркий,
 * неизвестный серым с «?», так что журнал сам подсказывает, чего не хватает.
 * Рецепт показывается иконками ингредиентов и результата.
 */
public final class JournalEntries {
    private JournalEntries() {
    }

    public record Node(String fact, String parent, int tab, int x, int y,
                       Supplier<ItemStack> icon, ItemStack[] ingredients, ItemStack result) {
        public String titleKey() {
            return "journal.canopy.node." + this.fact;
        }

        public String descKey() {
            return "journal.canopy.node." + this.fact + ".desc";
        }

        public String hintKey() {
            return "journal.canopy.node." + this.fact + ".hint";
        }
    }

    private static final List<Node> NODES = new ArrayList<>();
    private static boolean built;

    private static ItemStack stack(Supplier<? extends net.minecraft.world.item.Item> item) {
        return new ItemStack(item.get());
    }

    private static void add(String fact, String parent, int tab, int x, int y,
                            Supplier<ItemStack> icon, ItemStack[] ingredients, ItemStack result) {
        NODES.add(new Node(fact, parent, tab, x, y, icon, ingredients, result));
    }

    private static void build() {
        // Крафты — главное древо прогресса (вкладка 4).
        int T = 4;
        add(Journal.CRAFT_FLASK, null, T, 14, 46, () -> stack(CanopyItems.FLASK),
                new ItemStack[]{new ItemStack(Items.BAMBOO), new ItemStack(Items.STRING)}, stack(CanopyItems.FLASK));
        add(Journal.CRAFT_MACHETE, null, T, 46, 46, () -> stack(CanopyItems.STONE_MACHETE),
                new ItemStack[]{new ItemStack(Items.BAMBOO), new ItemStack(Items.FLINT)}, stack(CanopyItems.STONE_MACHETE));
        add(Journal.GOUGE, Journal.CRAFT_MACHETE, T, 78, 46, () -> stack(CanopyItems.GOUGE),
                new ItemStack[]{new ItemStack(Items.BAMBOO), new ItemStack(Items.FLINT)}, stack(CanopyItems.GOUGE));
        add(Journal.ROPE, null, T, 110, 46, () -> stack(CanopyItems.ROPE),
                new ItemStack[]{new ItemStack(Items.BAMBOO), stack(CanopyItems.SILK_POD)}, stack(CanopyItems.ROPE));

        add(Journal.CRAFT_BONE, Journal.CRAFT_MACHETE, T, 46, 102, () -> stack(CanopyItems.BONE_MACHETE),
                new ItemStack[]{stack(CanopyItems.BONE_SHARD), stack(CanopyItems.BONE_SHARD), new ItemStack(Items.BAMBOO)}, stack(CanopyItems.BONE_MACHETE));
        add(Journal.METAL, Journal.GOUGE, T, 78, 102, () -> stack(CanopyItems.METAL_MACHETE),
                new ItemStack[]{stack(CanopyItems.EXPOSED_VEIN), new ItemStack(Items.STICK)}, stack(CanopyItems.METAL_MACHETE));
        add(Journal.CAMP_BUILT, Journal.ROPE, T, 110, 102, () -> stack(CanopyItems.HAMMOCK),
                new ItemStack[]{new ItemStack(Items.BAMBOO), stack(CanopyItems.SILK_POD)}, stack(CanopyItems.HAMMOCK));

        add(Journal.CRAFT_FIBER, null, T, 14, 158, () -> stack(CanopyItems.FIBER_TUNIC),
                new ItemStack[]{new ItemStack(Items.BAMBOO), stack(CanopyItems.SILK_POD)}, stack(CanopyItems.FIBER_TUNIC));
        add(Journal.CRAFT_HIDE, Journal.CRAFT_FIBER, T, 46, 158, () -> stack(CanopyItems.HIDE_COAT),
                new ItemStack[]{stack(CanopyItems.HIDE)}, stack(CanopyItems.HIDE_COAT));
        add(Journal.CRAFT_RUBBER, Journal.CRAFT_HIDE, T, 78, 158, () -> stack(CanopyItems.RUBBER_SAP),
                new ItemStack[]{stack(CanopyItems.RUBBER_SAP)}, stack(CanopyItems.HIDE_COAT));
        add(Journal.CRAFT_BANDAGE, null, T, 142, 46, () -> stack(CanopyItems.BANDAGE),
                new ItemStack[]{new ItemStack(Items.BAMBOO), stack(CanopyItems.BITTER_HERB)}, stack(CanopyItems.BANDAGE));

        add(Journal.CRAFT_COOKED, null, T, 14, 102, () -> stack(CanopyItems.COOKED_TARO),
                new ItemStack[]{stack(CanopyItems.TARO_ROOT)}, stack(CanopyItems.COOKED_TARO));
        add(Journal.CRAFT_STEW, Journal.CRAFT_COOKED, T, 110, 158, () -> stack(CanopyItems.STEW),
                new ItemStack[]{stack(CanopyItems.CLAY_OVEN), stack(CanopyItems.TARO_ROOT)}, stack(CanopyItems.STEW));
        add(Journal.ANTIDOTE, Journal.CRAFT_BANDAGE, T, 142, 158, () -> stack(CanopyItems.ANTIDOTE),
                new ItemStack[]{stack(CanopyItems.BITTER_HERB), stack(CanopyItems.FLASK)}, stack(CanopyItems.ANTIDOTE));
        add(Journal.RESPIRATOR, Journal.CRAFT_BANDAGE, T, 142, 102, () -> stack(CanopyItems.RESPIRATOR),
                new ItemStack[]{stack(CanopyItems.AIR_PLANT), stack(CanopyItems.BANDAGE)}, stack(CanopyItems.RESPIRATOR));

        // Растения — вкладка 1.
        T = 1;
        add(Journal.TARO_SEEN, null, T, 14, 46, () -> stack(CanopyItems.TARO_ROOT), null, null);
        add(Journal.TARO_COOKED, Journal.TARO_SEEN, T, 46, 46, () -> stack(CanopyItems.COOKED_TARO),
                new ItemStack[]{stack(CanopyItems.TARO_ROOT)}, stack(CanopyItems.COOKED_TARO));
        add(Journal.BITTER_SEEN, null, T, 78, 46, () -> stack(CanopyItems.BITTER_HERB), null, null);
        add(Journal.AIR_PLANT, null, T, 110, 46, () -> stack(CanopyItems.AIR_PLANT), null, null);
        add(Journal.PLANT_LIANA, null, T, 14, 102, () -> stack(CanopyItems.WATER_LIANA), null, null);
        add(Journal.PLANT_BROMELIAD, Journal.PLANT_LIANA, T, 142, 46, () -> stack(CanopyItems.BROMELIAD), null, null);
        add(Journal.PLANT_HELICONIA, null, T, 78, 102, () -> stack(CanopyItems.HELICONIA), null, null);
        add(Journal.PLANT_GINGER, null, T, 110, 102, () -> stack(CanopyItems.GINGER_LILY), null, null);
        add(Journal.PLANT_FUNGUS, null, T, 46, 102, () -> stack(CanopyItems.LOG_FUNGUS), null, null);
        add("plant_banana", Journal.PLANT_FUNGUS, T, 14, 158, () -> stack(CanopyItems.BANANA), null, null);
        add("plant_orchid", Journal.PLANT_GINGER, T, 142, 102, () -> stack(CanopyItems.ORCHID),
                new ItemStack[]{stack(CanopyItems.ORCHID)}, new ItemStack(Items.MAGENTA_DYE));

        // Звери — вкладка 2.
        T = 2;
        add(Journal.AGOUTI, null, T, 14, 46, () -> stack(CanopyItems.AGOUTI_SPAWN_EGG), null, null);
        add(Journal.MONKEY, null, T, 46, 46, () -> stack(CanopyItems.MONKEY_SPAWN_EGG), null, null);
        add(Journal.TOUCAN, null, T, 78, 46, () -> stack(CanopyItems.TOUCAN_SPAWN_EGG), null, null);
        add(Journal.CAPYBARA, null, T, 110, 46, () -> stack(CanopyItems.CAPYBARA_SPAWN_EGG), null, null);
        add(Journal.TAPIR, null, T, 142, 46, () -> stack(CanopyItems.TAPIR_SPAWN_EGG), null, null);
        add(Journal.SNAKE, null, T, 14, 102, () -> stack(CanopyItems.SNAKE_SPAWN_EGG), null, null);
        add(Journal.CAIMAN, null, T, 46, 102, () -> stack(CanopyItems.CAIMAN_SPAWN_EGG), null, null);
        add(Journal.JAGUAR, null, T, 78, 102, () -> stack(CanopyItems.JAGUAR_SPAWN_EGG), null, null);
        add(Journal.GORILLA, null, T, 110, 102, () -> stack(CanopyItems.GORILLA_SPAWN_EGG), null, null);

        // Места и явления — вкладка 3.
        T = 3;
        add("place_lowland", null, T, 14, 46, () -> new ItemStack(Items.JUNGLE_LEAVES), null, null);
        add("place_bamboo_thicket", "place_lowland", T, 46, 46, () -> new ItemStack(Items.BAMBOO), null, null);
        add("place_flooded", "place_lowland", T, 14, 102, () -> new ItemStack(Items.MUD), null, null);
        add("place_highland", "place_bamboo_thicket", T, 78, 46, () -> new ItemStack(Items.MOSS_BLOCK), null, null);
        add("place_canyon", "place_flooded", T, 110, 46, () -> new ItemStack(Items.TERRACOTTA), null, null);
        add(Journal.CANOPY, "place_lowland", T, 142, 46, () -> new ItemStack(Items.JUNGLE_SAPLING), null, null);
        add(Journal.VEIN, "place_canyon", T, 46, 102, () -> stack(CanopyItems.EXPOSED_VEIN), null, null);
        add(Journal.GAS, Journal.VEIN, T, 78, 102, () -> stack(CanopyItems.GAS_SEEP), null, null);
        add(Journal.DOWNPOUR, null, T, 14, 158, () -> new ItemStack(Items.WATER_BUCKET), null, null);
        add(Journal.FLOOD, Journal.DOWNPOUR, T, 46, 158, () -> new ItemStack(Items.KELP), null, null);
        add(Journal.WET_SEASON, Journal.FLOOD, T, 78, 158, () -> new ItemStack(Items.CLOCK), null, null);
        add(Journal.CAMP, null, T, 110, 102, () -> stack(CanopyItems.EXPEDITION_CACHE), null, null);
    }

    public static List<Node> nodes(int tab) {
        if (!built) {
            build();
            built = true;
        }
        List<Node> out = new ArrayList<>();
        for (Node node : NODES) {
            if (node.tab() == tab) {
                out.add(node);
            }
        }
        return out;
    }

    public static Node byFact(String fact) {
        for (Node node : NODES) {
            if (node.fact().equals(fact)) {
                return node;
            }
        }
        return null;
    }
}
