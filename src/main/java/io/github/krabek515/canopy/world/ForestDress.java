package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ForestDress {
    public static final int VERSION = 4;

    private ForestDress() {
    }

    public static void dress(ChunkAccess chunk) {
        int x0 = chunk.getPos().getMinBlockX();
        int z0 = chunk.getPos().getMinBlockZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = x0; x < x0 + 16; x++) {
            for (int z = z0; z < z0 + 16; z++) {
                int top = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                String biome = biome(chunk, x, top, z);
                if (biome.isEmpty()) {
                    continue;
                }
                int from = Math.max(chunk.getMinBuildHeight(), top - 42);
                for (int y = from; y <= top; y++) {
                    pos.set(x, y, z);
                    BlockState state = chunk.getBlockState(pos);
                    BlockState next = convert(state);
                    if (next != null) {
                        chunk.setBlockState(pos, next, false);
                        state = next;
                    }
                    if (state.is(Blocks.COCOA) || looseVine(chunk, pos, state)) {
                        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
                        continue;
                    }
                    // Гевея: редкий каучуковый нарост низко на стволе влажного леса.
                    if (rubberHere(chunk, pos, state, x, z, biome)) {
                        chunk.setBlockState(pos, io.github.krabek515.canopy.block.CanopyBlocks.RUBBER_KNOT.get().defaultBlockState(), false);
                    }
                    // Банановая гроздь висит под кроной влажного леса — уже спелая.
                    if (state.isAir() && (biome.equals("lowland") || biome.equals("flooded")) && mix(x, y, z) % 57 == 0) {
                        pos.set(x, y + 1, z);
                        if (chunk.getBlockState(pos).is(Blocks.JUNGLE_LEAVES)) {
                            pos.set(x, y, z);
                            chunk.setBlockState(pos, io.github.krabek515.canopy.block.CanopyBlocks.BANANA_BUNCH.get()
                                    .defaultBlockState().setValue(io.github.krabek515.canopy.block.BananaBunchBlock.AGE, 2), false);
                            pos.set(x, y, z);
                        }
                        pos.set(x, y, z);
                    }
                    // Орхидея-эпифит на стволах и мхе.
                    if (state.isAir() && mix(x + 11, y, z) % 43 == 0) {
                        pos.set(x, y - 1, z);
                        BlockState under = chunk.getBlockState(pos);
                        pos.set(x, y, z);
                        if (under.is(net.minecraft.tags.BlockTags.LOGS) || under.is(Blocks.MOSS_BLOCK)) {
                            chunk.setBlockState(pos, io.github.krabek515.canopy.block.CanopyBlocks.ORCHID.get().defaultBlockState(), false);
                        }
                    }
                }
                soil(chunk, pos, x, top - 1, z, biome);
                if (biome.equals("canyon")) {
                    strata(chunk, pos, x, top, z);
                }
            }
        }
    }

    /**
     * Стенки каньона: открытый камень выше уровня реки ложится слоями обожжённой глины и спрессованного ила,
     * как срез породы. Камень внутри массива не трогаем, только тот, у которого есть воздух сбоку.
     */
    private static void strata(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int x, int top, int z) {
        int x0 = chunk.getPos().getMinBlockX();
        int z0 = chunk.getPos().getMinBlockZ();
        int shift = mix(x0, 0, z0) % 7;
        for (int y = Math.max(64, top - 18); y <= top; y++) {
            pos.set(x, y, z);
            BlockState state = chunk.getBlockState(pos);
            if (!state.is(Blocks.STONE) && !state.is(Blocks.ANDESITE) && !state.is(Blocks.GRANITE) && !state.is(Blocks.DIORITE)) {
                continue;
            }
            boolean exposed = false;
            for (Direction d : Direction.Plane.HORIZONTAL) {
                int nx = x + d.getStepX();
                int nz = z + d.getStepZ();
                if (nx < x0 || nx >= x0 + 16 || nz < z0 || nz >= z0 + 16) {
                    continue;
                }
                BlockState n = chunk.getBlockState(pos.set(nx, y, nz));
                if (n.isAir() || !n.getFluidState().isEmpty()) {
                    exposed = true;
                    break;
                }
            }
            pos.set(x, y, z);
            if (!exposed) {
                continue;
            }
            BlockState layer = switch (Math.floorMod(y / 2 + shift, 7)) {
                case 0, 4 -> Blocks.TERRACOTTA.defaultBlockState();
                case 1 -> Blocks.ORANGE_TERRACOTTA.defaultBlockState();
                case 2 -> Blocks.PACKED_MUD.defaultBlockState();
                case 3 -> Blocks.BROWN_TERRACOTTA.defaultBlockState();
                case 6 -> Blocks.RED_TERRACOTTA.defaultBlockState();
                default -> null;
            };
            if (layer != null) {
                chunk.setBlockState(pos, layer, false);
            }
        }
    }

    private static void soil(ChunkAccess chunk, BlockPos.MutableBlockPos pos, int x, int y, int z, String biome) {
        if (y <= chunk.getMinBuildHeight()) {
            return;
        }
        pos.set(x, y, z);
        BlockState state = chunk.getBlockState(pos);
        if (!state.is(Blocks.GRASS_BLOCK) && !state.is(Blocks.DIRT)) {
            return;
        }
        BlockState above = chunk.getBlockState(pos.above());
        if (!above.isAir() && above.getFluidState().isEmpty() && !above.is(BlockTags.REPLACEABLE) && !above.is(BlockTags.LEAVES)) {
            return;
        }
        int roll = mix(x, y, z) % 10;
        BlockState floor = switch (biome) {
            case "canyon" -> switch (roll) {
                case 0, 1, 2 -> Blocks.COARSE_DIRT.defaultBlockState();
                case 3 -> Blocks.PACKED_MUD.defaultBlockState();
                default -> null;
            };
            case "highland" -> switch (roll) {
                case 0, 1, 2 -> Blocks.MOSS_BLOCK.defaultBlockState();
                case 3, 4 -> Blocks.PODZOL.defaultBlockState();
                case 5 -> Blocks.ROOTED_DIRT.defaultBlockState();
                default -> null;
            };
            case "flooded" -> switch (roll) {
                case 0, 1, 2, 3 -> Blocks.MUD.defaultBlockState();
                case 4, 5, 6 -> Blocks.ROOTED_DIRT.defaultBlockState();
                case 7 -> Blocks.PODZOL.defaultBlockState();
                default -> null;
            };
            default -> switch (roll) {
                case 0, 1, 2 -> Blocks.ROOTED_DIRT.defaultBlockState();
                case 3, 4 -> Blocks.PODZOL.defaultBlockState();
                case 5, 6 -> Blocks.MUD.defaultBlockState();
                case 7 -> Blocks.COARSE_DIRT.defaultBlockState();
                default -> null;
            };
        };
        if (floor != null) {
            chunk.setBlockState(pos, floor, false);
        }
    }

    /**
     * Нарост сидит у самой земли: вертикальный ствол, а в трёх блоках под ним уже грунт.
     * Примерно один нарост на сорок стволовых брёвен.
     */
    private static boolean rubberHere(ChunkAccess chunk, BlockPos.MutableBlockPos pos, BlockState state, int x, int z, String biome) {
        if (!state.is(Blocks.JUNGLE_LOG) || !state.hasProperty(RotatedPillarBlock.AXIS)
                || state.getValue(RotatedPillarBlock.AXIS) != Direction.Axis.Y) {
            return false;
        }
        if (!(biome.equals("lowland") || biome.equals("flooded") || biome.equals("bamboo_thicket"))) {
            return false;
        }
        if (mix(x, pos.getY(), z) % 41 != 0) {
            return false;
        }
        for (int dy = 1; dy <= 3; dy++) {
            BlockState below = chunk.getBlockState(pos.set(x, pos.getY() - dy, z));
            pos.set(x, pos.getY() + dy, z);
            if (below.is(BlockTags.DIRT) || below.is(Blocks.MUD) || below.is(Blocks.CLAY) || below.is(Blocks.MOSS_BLOCK)
                    || below.is(Blocks.PODZOL) || below.is(Blocks.ROOTED_DIRT) || below.is(Blocks.COARSE_DIRT)) {
                return true;
            }
            if (!below.is(Blocks.JUNGLE_LOG)) {
                return false;
            }
        }
        return false;
    }

    private static boolean looseVine(ChunkAccess chunk, BlockPos pos, BlockState state) {
        if (!state.is(Blocks.VINE)) {
            return false;
        }
        if (state.getValue(VineBlock.UP)) {
            BlockState above = chunk.getBlockState(pos.above());
            if (above.is(BlockTags.LOGS) || above.is(BlockTags.LEAVES)) {
                return false;
            }
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!state.getValue(VineBlock.PROPERTY_BY_DIRECTION.get(direction))) {
                continue;
            }
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighbor = chunk.getBlockState(neighborPos);
            if (neighbor.is(BlockTags.LOGS) || neighbor.is(BlockTags.LEAVES) || neighbor.isFaceSturdy(chunk, neighborPos, direction.getOpposite())) {
                return false;
            }
        }
        return true;
    }

    private static BlockState convert(BlockState state) {
        if (isTemperateLog(state)) {
            BlockState log = Blocks.JUNGLE_LOG.defaultBlockState();
            if (state.hasProperty(RotatedPillarBlock.AXIS)) {
                log = log.setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            }
            return log;
        }
        if (isTemperateLeaf(state)) {
            BlockState leaves = Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, false);
            if (state.hasProperty(LeavesBlock.DISTANCE)) {
                leaves = leaves.setValue(LeavesBlock.DISTANCE, state.getValue(LeavesBlock.DISTANCE));
            }
            if (state.hasProperty(LeavesBlock.WATERLOGGED)) {
                leaves = leaves.setValue(LeavesBlock.WATERLOGGED, state.getValue(LeavesBlock.WATERLOGGED));
            }
            return leaves;
        }
        if (state.is(Blocks.DEAD_BUSH)) {
            return Blocks.AIR.defaultBlockState();
        }
        return null;
    }

    private static boolean isTemperateLog(BlockState state) {
        return state.is(Blocks.OAK_LOG) || state.is(Blocks.OAK_WOOD)
                || state.is(Blocks.SPRUCE_LOG) || state.is(Blocks.SPRUCE_WOOD)
                || state.is(Blocks.BIRCH_LOG) || state.is(Blocks.BIRCH_WOOD)
                || state.is(Blocks.DARK_OAK_LOG) || state.is(Blocks.DARK_OAK_WOOD)
                || state.is(Blocks.ACACIA_LOG) || state.is(Blocks.ACACIA_WOOD)
                || state.is(Blocks.MANGROVE_LOG) || state.is(Blocks.MANGROVE_WOOD)
                || state.is(Blocks.STRIPPED_OAK_LOG) || state.is(Blocks.STRIPPED_OAK_WOOD)
                || state.is(Blocks.STRIPPED_SPRUCE_LOG) || state.is(Blocks.STRIPPED_SPRUCE_WOOD)
                || state.is(Blocks.STRIPPED_BIRCH_LOG) || state.is(Blocks.STRIPPED_BIRCH_WOOD)
                || state.is(Blocks.STRIPPED_DARK_OAK_LOG) || state.is(Blocks.STRIPPED_DARK_OAK_WOOD)
                || state.is(Blocks.STRIPPED_ACACIA_LOG) || state.is(Blocks.STRIPPED_ACACIA_WOOD)
                || state.is(Blocks.STRIPPED_MANGROVE_LOG) || state.is(Blocks.STRIPPED_MANGROVE_WOOD);
    }

    private static boolean isTemperateLeaf(BlockState state) {
        return state.is(Blocks.OAK_LEAVES) || state.is(Blocks.SPRUCE_LEAVES)
                || state.is(Blocks.BIRCH_LEAVES) || state.is(Blocks.DARK_OAK_LEAVES)
                || state.is(Blocks.ACACIA_LEAVES) || state.is(Blocks.MANGROVE_LEAVES);
    }

    private static String biome(ChunkAccess chunk, int x, int y, int z) {
        return chunk.getNoiseBiome(x >> 2, y >> 2, z >> 2).unwrapKey()
                .filter(key -> Canopy.MOD_ID.equals(key.location().getNamespace()))
                .map(key -> key.location().getPath())
                .orElse("");
    }

    private static int mix(int x, int y, int z) {
        int n = x * 734287 + y * 297121 + z * 912931;
        n = (n ^ (n >>> 13)) * 1274126177;
        return n & Integer.MAX_VALUE;
    }
}
