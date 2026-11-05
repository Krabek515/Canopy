package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CanopyFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Canopy.MOD_ID);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ROTTEN_DECK = FEATURES.register("rotten_deck",
            () -> new RottenDeckFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MARKED_CARGO = FEATURES.register("marked_cargo",
            () -> new MarkedCargoFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GIANT_TREE = FEATURES.register("giant_tree",
            () -> new GiantTreeFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> EXPOSED_VEIN = FEATURES.register("exposed_vein",
            () -> new ExposedVeinFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> EXPEDITION_CAMP = FEATURES.register("expedition_camp",
            () -> new ExpeditionCampFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> GAS_POCKET = FEATURES.register("gas_pocket",
            () -> new GasPocketFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> UNDERGROWTH = FEATURES.register("undergrowth",
            () -> new UndergrowthFeature(NoneFeatureConfiguration.CODEC));
    // формы леса
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> JUNGLE_STAND = shape("jungle_stand", JungleTrees::stand);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> EMERGENT_TREE = shape("emergent_tree", JungleTrees::emergent);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> CANOPY_TREE = shape("canopy_tree", JungleTrees::canopyTree);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> UNDERSTORY_TREE = shape("understory_tree", JungleTrees::understory);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> PALM_TREE = shape("palm_tree", JungleTrees::palm);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> STILT_TREE = shape("stilt_tree", JungleTrees::stilt);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MOSS_TREE = shape("moss_tree", JungleTrees::mossTree);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> FALLEN_LOG = shape("fallen_log", JungleTrees::fallenLog);
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BUSH = shape("bush", JungleTrees::bush);

    private static RegistryObject<Feature<NoneFeatureConfiguration>> shape(String name, ShapeFeature.Shape shape) {
        return FEATURES.register(name, () -> new ShapeFeature(NoneFeatureConfiguration.CODEC, shape));
    }

    private CanopyFeatures() {
    }
}
