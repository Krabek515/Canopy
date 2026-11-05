package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.Canopy;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.TreeSet;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
public final class JungleWorld {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final TagKey<EntityType<?>> VANILLA_REMOVED = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "vanilla_removed"));
    public static final TagKey<EntityType<?>> JUNGLE_HUNTERS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "jungle_hunters"));

    private JungleWorld() {
    }

    @SubscribeEvent
    public static void onTravel(EntityTravelToDimensionEvent event) {
        if (event.getDimension() != Level.NETHER && event.getDimension() != Level.END) {
            return;
        }
        event.setCanceled(true);
        if (event.getEntity() instanceof Player player) {
            player.displayClientMessage(Component.translatable("message.canopy.dimension_closed"), true);
        }
    }

    @SubscribeEvent
    public static void onSpawn(MobSpawnEvent.FinalizeSpawn event) {
        Mob mob = event.getEntity();
        if (mob.getType().is(JUNGLE_HUNTERS)) {
            return;
        }
        boolean removedCreature = mob.getType().is(VANILLA_REMOVED);
        if (removedCreature || mob.getType().getCategory() == MobCategory.MONSTER) {
            event.setSpawnCancelled(true);
        }
    }

    @SubscribeEvent
    public static void onStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        GameRules rules = server.overworld().getGameRules();
        rules.getRule(GameRules.RULE_DOINSOMNIA).set(false, server);
        rules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, server);

        ServerLevel overworld = server.overworld();
        BlockPos spawn = overworld.getSharedSpawnPos();
        var biome = overworld.getBiome(spawn);
        LOGGER.info("Canopy spawn biome at {} is {}", spawn, biome.unwrapKey().map(Object::toString).orElse("unbound"));
        TreeSet<String> found = new TreeSet<>();
        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                BlockPos sample = spawn.offset(x * 320, 0, z * 320);
                found.add(overworld.getBiome(sample).unwrapKey().map(key -> key.location().toString()).orElse("unbound"));
            }
        }
        LOGGER.info("Canopy biome sample: {}", found);
        if (Boolean.getBoolean("canopy.census")) {
            LOGGER.info("Canopy census around spawn:\n{}", Census.report(overworld, spawn, 160));
        }
        var biomes = server.registryAccess().registryOrThrow(Registries.BIOME);
        for (String path : new String[]{"lowland", "bamboo_thicket", "flooded", "highland", "canyon"}) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, path);
            if (!biomes.containsKey(id)) {
                throw new IllegalStateException("Canopy biome missing: " + id);
            }
        }
    }
}
