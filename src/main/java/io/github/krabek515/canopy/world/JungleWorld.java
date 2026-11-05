package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.Canopy;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.TreeSet;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
public final class JungleWorld {
    private static final Logger LOGGER = LogUtils.getLogger();

    private JungleWorld() {
    }

    @SubscribeEvent
    public static void onStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
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
