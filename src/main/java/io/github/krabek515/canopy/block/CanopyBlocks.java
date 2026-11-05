package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.block.Block;

public final class CanopyBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Canopy.MOD_ID);
    public static final RegistryObject<Block> THORNY_BRUSH = BLOCKS.register("thorny_brush", () -> new ThornyBrushBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .strength(0.5f)
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)
                    .offsetType(BlockBehaviour.OffsetType.NONE)
    ));
    public static final RegistryObject<Block> TARO = BLOCKS.register("taro", () -> new TaroBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)
                    .offsetType(BlockBehaviour.OffsetType.NONE)
    ));
    public static final RegistryObject<Block> BITTER_HERB = BLOCKS.register("bitter_herb", () -> new TaroBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)
                    .offsetType(BlockBehaviour.OffsetType.NONE)
    ));
    public static final RegistryObject<Block> ROTTEN_DECK = BLOCKS.register("rotten_deck", () -> new RottenDeckBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.8f)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final RegistryObject<Block> SILK_POD = BLOCKS.register("silk_pod", () -> new SilkPodBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .instabreak()
                    .sound(SoundType.WOOL)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final RegistryObject<Block> EXPOSED_VEIN = BLOCKS.register("exposed_vein", () -> new Block(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.5f, 6f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
    ));
    public static final RegistryObject<Block> ROPE = BLOCKS.register("rope", () -> new RopeBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.4f)
                    .sound(SoundType.BAMBOO)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final RegistryObject<Block> GAS_SEEP = BLOCKS.register("gas_seep", () -> new Block(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.5f)
                    .sound(SoundType.MUD)
    ));
    public static final RegistryObject<Block> AIR_PLANT = BLOCKS.register("air_plant", () -> new TaroBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY)
                    .offsetType(BlockBehaviour.OffsetType.NONE)
    ));
    public static final RegistryObject<Block> EXPEDITION_CACHE = BLOCKS.register("expedition_cache", () -> new ExpeditionCacheBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.4f)
                    .sound(SoundType.WOOL)
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final RegistryObject<Block> MARKED_CARGO = BLOCKS.register("marked_cargo", () -> new MarkedCargoBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(0.4f)
                    .sound(SoundType.WOOL)
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final RegistryObject<Block> HELICONIA = BLOCKS.register("heliconia", () -> new TaroBlock(plant(MapColor.COLOR_RED)));
    public static final RegistryObject<Block> GINGER_LILY = BLOCKS.register("ginger_lily", () -> new TaroBlock(plant(MapColor.SNOW)));
    public static final RegistryObject<Block> BROMELIAD = BLOCKS.register("bromeliad", () -> new BromeliadBlock(plant(MapColor.PLANT)));
    public static final RegistryObject<Block> LOG_FUNGUS = BLOCKS.register("log_fungus", () -> new LogFungusBlock(plant(MapColor.COLOR_ORANGE)));
    public static final RegistryObject<Block> WATER_LIANA = BLOCKS.register("water_liana", () -> new LianaBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.6f)
                    .sound(SoundType.VINE)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
    ));

    public static final RegistryObject<Block> RUBBER_KNOT = BLOCKS.register("rubber_knot", () -> new RubberKnotBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.6f)
                    .sound(SoundType.WOOD)
    ));
    public static final RegistryObject<Block> CLAY_OVEN = BLOCKS.register("clay_oven", () -> new ClayOvenBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .strength(1.2f)
                    .sound(SoundType.MUD_BRICKS)
                    .noOcclusion()
    ));
    public static final RegistryObject<Block> STILT_PLATFORM = BLOCKS.register("stilt_platform", () -> new StiltPlatformBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(0.8f)
                    .sound(SoundType.BAMBOO)
                    .noOcclusion()
    ));
    public static final RegistryObject<Block> HAMMOCK = BLOCKS.register("hammock", () -> new HammockBlock(
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOL)
                    .strength(0.4f)
                    .sound(SoundType.WOOL)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY)
    ));

    private static BlockBehaviour.Properties plant(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(PushReaction.DESTROY)
                .offsetType(BlockBehaviour.OffsetType.NONE);
    }

    private CanopyBlocks() {
    }
}
