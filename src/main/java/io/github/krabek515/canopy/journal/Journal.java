package io.github.krabek515.canopy.journal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Journal {
    public static final String TARO_SEEN = "taro_seen";
    public static final String TARO_RAW = "taro_raw";
    public static final String TARO_COOKED = "taro_cooked";
    public static final String JAGUAR = "beast_jaguar";
    public static final String JAGUAR_BITE = "beast_jaguar_bite";
    public static final String AGOUTI = "beast_agouti";
    public static final String SNAKE = "beast_snake";
    public static final String SNAKE_BITE = "beast_snake_bite";
    public static final String CRAFT_FLASK = "craft_flask";
    public static final String CRAFT_MACHETE = "craft_machete";
    public static final String CRAFT_COOKED = "craft_cooked";
    public static final String CRAFT_FIBER = "craft_fiber";
    public static final String CRAFT_HIDE = "craft_hide";
    public static final String CRAFT_BANDAGE = "craft_bandage";
    public static final String BITTER_SEEN = "bitter_seen";
    public static final String BAD_BREW = "bad_brew";
    public static final String ANTIDOTE = "antidote";
    public static final String MONKEY = "beast_monkey";
    public static final String MONKEY_RAID = "beast_monkey_raid";
    public static final String DOWNPOUR = "place_downpour";
    public static final String FLOOD = "place_flood";
    public static final String LIVING_FLOOR = "place_living_floor";
    public static final String BAIT = "place_bait";
    public static final String CANOPY = "place_canopy";
    public static final String ROPE = "craft_rope";
    public static final String VEIN = "place_vein";
    public static final String GOUGE = "craft_gouge";
    public static final String METAL = "craft_metal";
    public static final String CAMP = "place_camp";
    public static final String GAS = "place_gas";
    public static final String AIR_PLANT = "air_plant";
    public static final String RESPIRATOR = "craft_respirator";
    public static final String CAPYBARA = "beast_capybara";
    public static final String TAPIR = "beast_tapir";
    public static final String CAIMAN = "beast_caiman";
    public static final String CAIMAN_BITE = "beast_caiman_bite";
    public static final String GORILLA = "beast_gorilla";
    public static final String GORILLA_HIT = "beast_gorilla_hit";
    public static final String TOUCAN = "beast_toucan";
    public static final String WET_SEASON = "place_wet_season";
    public static final String CAMP_BUILT = "place_camp_built";
    public static final String CRAFT_BONE = "craft_bone";
    public static final String CRAFT_RUBBER = "craft_rubber";
    public static final String CRAFT_STEW = "craft_stew";
    public static final String PLANT_LIANA = "plant_liana";
    public static final String PLANT_BROMELIAD = "plant_bromeliad";
    public static final String PLANT_HELICONIA = "plant_heliconia";
    public static final String PLANT_GINGER = "plant_ginger";
    public static final String PLANT_FUNGUS = "plant_fungus";

    private final Set<String> facts = new HashSet<>();
    private boolean dirty = true;

    public boolean knows(String fact) {
        return this.facts.contains(fact);
    }

    public boolean learn(String fact) {
        if (this.facts.add(fact)) {
            this.dirty = true;
            return true;
        }
        return false;
    }

    public Set<String> facts() {
        return Set.copyOf(this.facts);
    }

    public void replace(Collection<String> facts) {
        this.facts.clear();
        this.facts.addAll(facts);
        this.dirty = false;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void clearDirty() {
        this.dirty = false;
    }

    public void copyFrom(Journal other) {
        this.facts.clear();
        this.facts.addAll(other.facts);
        this.dirty = true;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (String fact : this.facts) {
            list.add(StringTag.valueOf(fact));
        }
        tag.put("Facts", list);
        return tag;
    }

    public void load(CompoundTag tag) {
        this.facts.clear();
        ListTag list = tag.getList("Facts", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            this.facts.add(list.getString(i));
        }
        this.dirty = true;
    }
}
