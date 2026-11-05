package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.body.CanopyEffects;
import io.github.krabek515.canopy.body.Gas;
import io.github.krabek515.canopy.body.HeatSources;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.world.Shelter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public final class JournalPages {
    private JournalPages() {
    }

    public static Component page(Player player, Journal journal, int tab) {
        List<String> keys = switch (tab) {
            case 1 -> plants(journal);
            case 2 -> beasts(journal);
            case 3 -> places(journal);
            case 4 -> craft(journal);
            default -> now(player, journal);
        };
        if (keys.isEmpty()) {
            keys = List.of("journal.canopy.empty");
        }
        MutableComponent text = Component.empty();
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                text.append("\n");
            }
            text.append(Component.translatable(keys.get(i)));
        }
        return text;
    }

    private static List<String> now(Player player, Journal journal) {
        List<String> keys = new ArrayList<>();
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            long now = player.level().getGameTime();
            if (now < stats.floodWaveUntil()) {
                keys.add("journal.canopy.now.flood_wave");
            } else if (now < stats.floodWarnUntil()) {
                keys.add("journal.canopy.now.flood_warn");
            }
            if (Gas.bad(player)) {
                if (Gas.filtered(player)) {
                    keys.add("journal.canopy.now.gas_filter");
                } else if (journal.knows(Journal.RESPIRATOR)) {
                    keys.add("journal.canopy.now.gas_mask");
                } else {
                    keys.add("journal.canopy.now.gas");
                }
            }
            if (stats.wound() == SurvivalStats.WOUND_FEVER) {
                keys.add("journal.canopy.now.fever");
            } else if (stats.wound() == SurvivalStats.WOUND_INFECTED) {
                keys.add("journal.canopy.now.infected");
            } else if (stats.wound() == SurvivalStats.WOUND_OPEN && stats.woundDirty()) {
                keys.add("journal.canopy.now.wound_dirty");
            } else if (stats.wound() == SurvivalStats.WOUND_OPEN) {
                keys.add("journal.canopy.now.wound");
            }
            if (player.level().isThundering() && player.level().isRaining()) {
                keys.add(Shelter.covered(player) ? "journal.canopy.now.downpour_roof" : "journal.canopy.now.downpour");
            }
            if (player.hasEffect(CanopyEffects.VENOM.get())) {
                keys.add(journal.knows(Journal.ANTIDOTE) ? "journal.canopy.now.venom_brew" : "journal.canopy.now.venom");
            }
            if (stats.wound() == SurvivalStats.WOUND_NONE && stats.isBleeding(player.level().getGameTime())) {
                keys.add("journal.canopy.now.bleeding");
            }
            if (stats.thirst() <= 6f) {
                keys.add(journal.knows(Journal.CRAFT_FLASK) ? "journal.canopy.now.thirst_flask" : "journal.canopy.now.thirst");
            }
            if (player.getFoodData().getFoodLevel() <= 6) {
                keys.add(journal.knows(Journal.CRAFT_COOKED) ? "journal.canopy.now.food_fire" : "journal.canopy.now.food");
            }
            if (stats.temperature() >= 76f) {
                keys.add("journal.canopy.now.hot");
            } else if (stats.temperature() <= 24f) {
                keys.add("journal.canopy.now.cold");
            }
            if (stats.hand() >= SurvivalStats.HAND_INJURED) {
                keys.add("journal.canopy.now.hand");
            }
            if (stats.wetness() > 0.75f) {
                keys.add("journal.canopy.now.wet_clothes");
            }
            boolean fire = HeatSources.near(player, 5);
            if (!fire && !player.level().isDay()) {
                keys.add("journal.canopy.now.night");
            } else if (fire && keys.isEmpty()) {
                keys.add("journal.canopy.now.fire");
            }
            String next = path(journal);
            if (keys.isEmpty()) {
                keys.add(next);
            } else if (keys.size() < 4 && !keys.contains(next)) {
                keys.add(next);
            }
        });
        if (keys.isEmpty()) {
            keys.add(path(journal));
        }
        return keys.size() > 4 ? keys.subList(0, 4) : keys;
    }

    private static String path(Journal journal) {
        if (!journal.knows(Journal.CRAFT_FLASK)) {
            return "journal.canopy.now.path_flask";
        }
        if (!journal.knows(Journal.CRAFT_MACHETE)) {
            return "journal.canopy.now.path_machete";
        }
        if (!journal.knows(Journal.TARO_SEEN)) {
            return "journal.canopy.now.path_taro";
        }
        if (!journal.knows(Journal.CRAFT_COOKED)) {
            return "journal.canopy.now.path_fire";
        }
        if (!journal.knows(Journal.CRAFT_BANDAGE)) {
            return "journal.canopy.now.path_bandage";
        }
        if (!journal.knows(Journal.CANOPY)) {
            return "journal.canopy.now.path_tree";
        }
        if (!journal.knows(Journal.VEIN)) {
            return "journal.canopy.now.path_canyon";
        }
        return "journal.canopy.now.path_river";
    }

    private static List<String> plants(Journal journal) {
        List<String> keys = new ArrayList<>();
        if (journal.knows(Journal.TARO_SEEN)) {
            keys.add("journal.canopy.plants.taro");
        }
        if (journal.knows(Journal.TARO_RAW)) {
            keys.add("journal.canopy.plants.taro_raw");
        }
        if (journal.knows(Journal.TARO_COOKED)) {
            keys.add("journal.canopy.plants.taro_cooked");
        }
        if (journal.knows(Journal.BITTER_SEEN)) {
            keys.add("journal.canopy.plants.bitter");
        }
        if (journal.knows(Journal.AIR_PLANT)) {
            keys.add("journal.canopy.plants.air");
        }
        if (journal.knows(Journal.PLANT_LIANA)) {
            keys.add("journal.canopy.plants.liana");
        }
        if (journal.knows(Journal.PLANT_BROMELIAD)) {
            keys.add("journal.canopy.plants.bromeliad");
        }
        if (journal.knows(Journal.PLANT_HELICONIA)) {
            keys.add("journal.canopy.plants.heliconia");
        }
        if (journal.knows(Journal.PLANT_GINGER)) {
            keys.add("journal.canopy.plants.ginger");
        }
        if (journal.knows(Journal.PLANT_FUNGUS)) {
            keys.add("journal.canopy.plants.fungus");
        }
        if (journal.knows(Journal.BAD_BREW)) {
            keys.add("journal.canopy.plants.bad_brew");
        }
        if (journal.knows(Journal.ANTIDOTE)) {
            keys.add("journal.canopy.plants.antidote");
        }
        return keys;
    }

    private static List<String> beasts(Journal journal) {
        List<String> keys = new ArrayList<>();
        if (journal.knows(Journal.JAGUAR)) {
            keys.add("journal.canopy.beasts.jaguar");
        }
        if (journal.knows(Journal.JAGUAR_BITE)) {
            keys.add("journal.canopy.beasts.jaguar_bite");
        }
        if (journal.knows(Journal.AGOUTI)) {
            keys.add("journal.canopy.beasts.agouti");
        }
        if (journal.knows(Journal.SNAKE)) {
            keys.add("journal.canopy.beasts.snake");
        }
        if (journal.knows(Journal.SNAKE_BITE)) {
            keys.add("journal.canopy.beasts.snake_bite");
        }
        if (journal.knows(Journal.MONKEY)) {
            keys.add("journal.canopy.beasts.monkey");
        }
        if (journal.knows(Journal.MONKEY_RAID)) {
            keys.add("journal.canopy.beasts.monkey_raid");
        }
        for (String fact : new String[]{Journal.CAPYBARA, Journal.TAPIR, Journal.CAIMAN, Journal.CAIMAN_BITE, Journal.GORILLA, Journal.GORILLA_HIT, Journal.TOUCAN}) {
            if (journal.knows(fact)) {
                keys.add("journal.canopy.beasts." + fact.substring("beast_".length()));
            }
        }
        return keys;
    }

    private static List<String> places(Journal journal) {
        List<String> keys = new ArrayList<>();
        addPlace(keys, journal, "lowland");
        addPlace(keys, journal, "bamboo_thicket");
        addPlace(keys, journal, "flooded");
        addPlace(keys, journal, "highland");
        addPlace(keys, journal, "canyon");
        if (journal.knows(Journal.DOWNPOUR)) {
            keys.add("journal.canopy.places.downpour");
        }
        if (journal.knows(Journal.FLOOD)) {
            keys.add("journal.canopy.places.flood_wash");
        }
        if (journal.knows(Journal.LIVING_FLOOR)) {
            keys.add("journal.canopy.places.living_floor");
        }
        if (journal.knows(Journal.BAIT)) {
            keys.add("journal.canopy.places.bait");
        }
        if (journal.knows(Journal.CANOPY)) {
            keys.add("journal.canopy.places.canopy");
        }
        if (journal.knows(Journal.VEIN)) {
            keys.add("journal.canopy.places.vein");
        }
        if (journal.knows(Journal.CAMP)) {
            keys.add("journal.canopy.places.camp");
        }
        if (journal.knows(Journal.GAS)) {
            keys.add("journal.canopy.places.gas");
        }
        return keys;
    }

    private static void addPlace(List<String> keys, Journal journal, String path) {
        if (journal.knows("place_" + path)) {
            keys.add("journal.canopy.places." + path);
        }
    }

    private static List<String> craft(Journal journal) {
        List<String> keys = new ArrayList<>();
        if (journal.knows(Journal.CRAFT_FLASK)) {
            keys.add("journal.canopy.craft.flask");
        }
        if (journal.knows(Journal.CRAFT_MACHETE)) {
            keys.add("journal.canopy.craft.machete");
        }
        if (journal.knows(Journal.CRAFT_COOKED)) {
            keys.add("journal.canopy.craft.cooked");
        }
        if (journal.knows(Journal.CRAFT_FIBER)) {
            keys.add("journal.canopy.craft.fiber");
        }
        if (journal.knows(Journal.CRAFT_HIDE)) {
            keys.add("journal.canopy.craft.hide");
        }
        if (journal.knows(Journal.CRAFT_BANDAGE)) {
            keys.add("journal.canopy.craft.bandage");
        }
        if (journal.knows(Journal.ROPE)) {
            keys.add("journal.canopy.craft.rope");
        }
        if (journal.knows(Journal.GOUGE)) {
            keys.add("journal.canopy.craft.gouge");
        }
        if (journal.knows(Journal.METAL)) {
            keys.add("journal.canopy.craft.metal");
        }
        if (journal.knows(Journal.RESPIRATOR)) {
            keys.add("journal.canopy.craft.respirator");
        }
        return keys;
    }
}
