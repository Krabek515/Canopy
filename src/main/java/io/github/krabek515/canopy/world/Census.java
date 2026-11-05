package io.github.krabek515.canopy.world;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.TreeMap;

/** Перепись леса в загруженных чанках: сколько каких блоков и биомов вокруг точки. Инструмент проверки генерации. */
public final class Census {
    private Census() {
    }

    public static String report(ServerLevel level, BlockPos center, int radius) {
        Map<String, Integer> counts = new TreeMap<>();
        Map<String, Integer> biomes = new TreeMap<>();
        int chunks = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int cx = (center.getX() - radius) >> 4; cx <= (center.getX() + radius) >> 4; cx++) {
            for (int cz = (center.getZ() - radius) >> 4; cz <= (center.getZ() + radius) >> 4; cz++) {
                ChunkAccess chunk = level.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) {
                    continue;
                }
                chunks++;
                String biome = chunk.getNoiseBiome(cx << 2, 16, cz << 2).unwrapKey().map(k -> k.location().getPath()).orElse("?");
                biomes.merge(biome, 1, Integer::sum);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        int top = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                        for (int y = Math.max(chunk.getMinBuildHeight(), top - 40); y <= top; y++) {
                            BlockState state = chunk.getBlockState(pos.set(x, y, z));
                            if (state.isAir()) {
                                continue;
                            }
                            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                            if (id == null) {
                                continue;
                            }
                            String key = id.toString();
                            if (interesting(key)) {
                                counts.merge(key, 1, Integer::sum);
                            }
                        }
                    }
                }
            }
        }
        Map<String, Integer> beasts = new TreeMap<>();
        for (net.minecraft.world.entity.Entity e : level.getAllEntities()) {
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
            if (id != null) {
                beasts.merge(id.toString(), 1, Integer::sum);
            }
        }
        StringBuilder sb = new StringBuilder("chunks=" + chunks + " biomes=" + biomes + "\nentities=" + beasts + "\n");
        counts.forEach((k, v) -> sb.append("  ").append(k).append('=').append(v).append('\n'));
        return sb.toString();
    }

    private static boolean interesting(String key) {
        return key.startsWith("canopy:") || key.contains("log") || key.contains("leaves") || key.contains("roots")
                || key.contains("terracotta") || key.contains("moss") || key.contains("vine") || key.contains("lichen")
                || key.contains("fern") || key.contains("spore") || key.contains("lily") || key.contains("mud")
                || key.contains("bamboo") || key.contains("campfire") || key.contains("dripstone");
    }
}
