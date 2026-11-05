package io.github.krabek515.canopy.entity;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CanopyEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Canopy.MOD_ID);

    public static final RegistryObject<EntityType<Jaguar>> JAGUAR = ENTITIES.register("jaguar", () ->
            EntityType.Builder.of(Jaguar::new, MobCategory.CREATURE).sized(0.85f, 0.8f).clientTrackingRange(10).build("jaguar"));
    public static final RegistryObject<EntityType<Agouti>> AGOUTI = ENTITIES.register("agouti", () ->
            EntityType.Builder.of(Agouti::new, MobCategory.CREATURE).sized(0.45f, 0.5f).clientTrackingRange(8).build("agouti"));
    public static final RegistryObject<EntityType<Snake>> SNAKE = ENTITIES.register("snake", () ->
            EntityType.Builder.of(Snake::new, MobCategory.CREATURE).sized(0.7f, 0.35f).clientTrackingRange(8).build("snake"));
    public static final RegistryObject<EntityType<Monkey>> MONKEY = ENTITIES.register("monkey", () ->
            EntityType.Builder.of(Monkey::new, MobCategory.CREATURE).sized(0.5f, 0.9f).clientTrackingRange(8).build("monkey"));
    public static final RegistryObject<EntityType<Capybara>> CAPYBARA = ENTITIES.register("capybara", () ->
            EntityType.Builder.of(Capybara::new, MobCategory.CREATURE).sized(0.9f, 0.8f).clientTrackingRange(10).build("capybara"));
    public static final RegistryObject<EntityType<Tapir>> TAPIR = ENTITIES.register("tapir", () ->
            EntityType.Builder.of(Tapir::new, MobCategory.CREATURE).sized(1.0f, 1.1f).clientTrackingRange(10).build("tapir"));
    public static final RegistryObject<EntityType<Caiman>> CAIMAN = ENTITIES.register("caiman", () ->
            EntityType.Builder.of(Caiman::new, MobCategory.CREATURE).sized(1.1f, 0.5f).clientTrackingRange(10).build("caiman"));
    public static final RegistryObject<EntityType<Gorilla>> GORILLA = ENTITIES.register("gorilla", () ->
            EntityType.Builder.of(Gorilla::new, MobCategory.CREATURE).sized(1.1f, 1.5f).clientTrackingRange(10).build("gorilla"));
    public static final RegistryObject<EntityType<Toucan>> TOUCAN = ENTITIES.register("toucan", () ->
            EntityType.Builder.of(Toucan::new, MobCategory.CREATURE).sized(0.5f, 0.6f).clientTrackingRange(8).build("toucan"));

    private CanopyEntities() {
    }

    public static void registerSpawns() {
        SpawnPlacements.register(JAGUAR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeastSpawn::shade);
        SpawnPlacements.register(AGOUTI.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeastSpawn::shade);
        SpawnPlacements.register(SNAKE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeastSpawn::shade);
        SpawnPlacements.register(CAPYBARA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeastSpawn::shore);
        SpawnPlacements.register(TAPIR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeastSpawn::shade);
        SpawnPlacements.register(CAIMAN.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeastSpawn::shallows);
        SpawnPlacements.register(GORILLA.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BeastSpawn::shade);
        SpawnPlacements.register(TOUCAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, BeastSpawn::canopy);
    }

    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(JAGUAR.get(), Monster.createMonsterAttributes().build());
        event.put(AGOUTI.get(), Rabbit.createAttributes().build());
        event.put(SNAKE.get(), Monster.createMonsterAttributes().build());
        event.put(MONKEY.get(), Monster.createMonsterAttributes().build());
        event.put(CAPYBARA.get(), Monster.createMonsterAttributes().build());
        event.put(TAPIR.get(), Monster.createMonsterAttributes().build());
        event.put(CAIMAN.get(), Monster.createMonsterAttributes().build());
        event.put(GORILLA.get(), Monster.createMonsterAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_KNOCKBACK).build());
        event.put(TOUCAN.get(), Monster.createMonsterAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED).build());
    }
}
