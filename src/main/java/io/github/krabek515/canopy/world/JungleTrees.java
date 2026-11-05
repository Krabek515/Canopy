package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.block.CanopyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Формы деревьев Canopy. Все они ставят ванильные джунглевые брёвна и листву по ссылке,
 * а своё добавляют растениями: лианой-водоносом, бромелией, трутовиком.
 *
 * Лес читается по ярусам: эмерджент выше всех, дерево полога с ветвями, подрост у земли,
 * пальма у воды и в бамбуке, дерево на ходулях в топи, моховое дерево в туманном лесу, упавший ствол везде.
 */
public final class JungleTrees {
    private static final BlockState LEAVES = Blocks.JUNGLE_LEAVES.defaultBlockState()
            .setValue(LeavesBlock.PERSISTENT, true)
            .setValue(LeavesBlock.DISTANCE, 1);

    private JungleTrees() {
    }

    // ---- формы ----

    /** Эмерджент: голый ствол 18–24, контрфорсы у земли, редкие длинные ветви и плоская широкая крона над пологом. */
    public static boolean emergent(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 18 + random.nextInt(7);
        if (!fits(level, origin, height + 2) || !soil(level, origin.below())) {
            return false;
        }
        trunk(level, origin, height);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            buttress(level, origin, d, 2 + random.nextInt(2));
        }
        int branches = 4 + random.nextInt(3);
        for (int i = 0; i < branches; i++) {
            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int y = height - 6 + random.nextInt(5);
            int len = 2 + random.nextInt(3);
            BlockPos end = branch(level, origin.above(y), d, len, random.nextInt(3) == 0);
            ball(level, end, 2, 1, random);
        }
        ball(level, origin.above(height), 3, 1, random);
        ball(level, origin.above(height + 1), 2, 0, random);
        hangVines(level, origin.above(height - 2), 4, random, 4);
        hangLianas(level, origin.above(height - 2), 4, random, 3);
        return true;
    }

    /** Дерево полога: ствол 10–15, две-четыре ветви с собственными кронами, лианы вниз. */
    public static boolean canopyTree(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 10 + random.nextInt(6);
        if (!fits(level, origin, height + 1) || !soil(level, origin.below())) {
            return false;
        }
        trunk(level, origin, height);
        if (random.nextInt(3) == 0) {
            buttress(level, origin, Direction.Plane.HORIZONTAL.getRandomDirection(random), 1);
        }
        int branches = 2 + random.nextInt(3);
        for (int i = 0; i < branches; i++) {
            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int y = height - 4 + random.nextInt(4);
            BlockPos end = branch(level, origin.above(y), d, 1 + random.nextInt(3), random.nextBoolean());
            ball(level, end, 2, 1, random);
            if (random.nextInt(4) == 0) {
                plant(level, end.above(), CanopyBlocks.BROMELIAD.get().defaultBlockState());
            }
        }
        ball(level, origin.above(height), 2 + random.nextInt(2), 1, random);
        hangVines(level, origin.above(height - 1), 3, random, 3);
        if (random.nextInt(3) == 0) {
            hangLianas(level, origin.above(height - 1), 3, random, 2);
        }
        if (random.nextInt(5) == 0) {
            plant(level, origin.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random)), CanopyBlocks.LOG_FUNGUS.get().defaultBlockState());
        }
        return true;
    }

    /** Подрост: короткий ствол и круглая крона у земли, где тень. */
    public static boolean understory(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 3 + random.nextInt(4);
        if (!fits(level, origin, height + 1) || !soil(level, origin.below())) {
            return false;
        }
        trunk(level, origin, height);
        ball(level, origin.above(height), random.nextInt(3) == 0 ? 1 : 2, 1, random);
        return true;
    }

    /** Куст: один пенёк и низкая шапка листвы, в которой прячутся змея и кошка. */
    public static boolean bush(LevelAccessor level, BlockPos origin, RandomSource random) {
        if (!open(level, origin) || !soil(level, origin.below())) {
            return false;
        }
        level.setBlock(origin, Blocks.JUNGLE_LOG.defaultBlockState(), 2);
        ball(level, origin, 1 + random.nextInt(2), 0, random);
        return true;
    }

    /** Пальма: светлый ствол с наклоном, веер из листвы на макушке. */
    public static boolean palm(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 7 + random.nextInt(5);
        if (!fits(level, origin, height + 2) || !soil(level, origin.below())) {
            return false;
        }
        Direction lean = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int leanAt = height / 2 + random.nextInt(2);
        BlockState trunk = Blocks.STRIPPED_JUNGLE_LOG.defaultBlockState();
        BlockPos pos = origin;
        for (int y = 0; y < height; y++) {
            if (y == leanAt) {
                pos = pos.relative(lean);
                if (!open(level, pos)) {
                    return false;
                }
            }
            level.setBlock(pos, trunk, 2);
            pos = pos.above();
        }
        BlockPos top = pos.below();
        set(level, top.above(), LEAVES);
        set(level, top.above(2), LEAVES);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            frond(level, top.above(), d, 3 + random.nextInt(2));
            Direction side = d.getClockWise();
            BlockPos diag = top.above().relative(d).relative(side);
            set(level, diag, LEAVES);
            set(level, diag.relative(d).relative(side), LEAVES);
            set(level, diag.relative(d).relative(side).relative(d).below(), LEAVES);
        }
        return true;
    }

    /** Дерево на ходулях: корни мангра из воды или ила, ствол начинается выше, крона как у дерева полога. */
    public static boolean stilt(LevelAccessor level, BlockPos origin, RandomSource random) {
        BlockPos base = origin;
        // origin стоит на поверхности воды или земли; нам нужен низ до дна
        int depth = 0;
        while (depth < 4 && level.getFluidState(base.below()).is(FluidTags.WATER)) {
            base = base.below();
            depth++;
        }
        if (!soil(level, base.below()) && !level.getBlockState(base.below()).is(Blocks.MUD)) {
            return false;
        }
        int height = 7 + random.nextInt(4);
        BlockPos trunkBase = origin.above(2);
        if (!fits(level, trunkBase, height + 1)) {
            return false;
        }
        BlockState roots = Blocks.MANGROVE_ROOTS.defaultBlockState();
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                for (BlockPos p = base.offset(dx, 0, dz); p.getY() <= origin.getY() + 1; p = p.above()) {
                    waterloggable(level, p, roots);
                }
            }
        }
        for (BlockPos p = base; p.getY() < trunkBase.getY(); p = p.above()) {
            waterloggable(level, p, roots);
        }
        trunk(level, trunkBase, height);
        int branches = 2 + random.nextInt(2);
        for (int i = 0; i < branches; i++) {
            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos end = branch(level, trunkBase.above(height - 3 + random.nextInt(3)), d, 1 + random.nextInt(2), false);
            ball(level, end, 2, 1, random);
            if (random.nextBoolean()) {
                set(level, end.below(), Blocks.HANGING_ROOTS.defaultBlockState());
            }
        }
        ball(level, trunkBase.above(height), 2, 1, random);
        hangVines(level, trunkBase.above(height - 1), 3, random, 4);
        hangLianas(level, trunkBase.above(height - 1), 3, random, 2);
        return true;
    }

    /** Моховое дерево туманного леса: лишайник по стволу, мох на ветвях, споровые цветы под ними. */
    public static boolean mossTree(LevelAccessor level, BlockPos origin, RandomSource random) {
        int height = 9 + random.nextInt(5);
        if (!fits(level, origin, height + 1) || !soil(level, origin.below())) {
            return false;
        }
        trunk(level, origin, height);
        for (int y = 1; y < height - 2; y++) {
            if (random.nextInt(3) != 0) {
                continue;
            }
            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos at = origin.above(y).relative(d);
            if (level.getBlockState(at).isAir()) {
                level.setBlock(at, Blocks.GLOW_LICHEN.defaultBlockState()
                        .setValue(MultifaceBlock.getFaceProperty(d.getOpposite()), true), 2);
            }
        }
        int branches = 2 + random.nextInt(3);
        for (int i = 0; i < branches; i++) {
            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos end = branch(level, origin.above(height - 4 + random.nextInt(4)), d, 1 + random.nextInt(3), random.nextBoolean());
            ball(level, end, 2, 1, random);
            BlockPos under = end.below();
            if (level.getBlockState(under).isAir()) {
                set(level, under, random.nextBoolean() ? Blocks.SPORE_BLOSSOM.defaultBlockState() : Blocks.HANGING_ROOTS.defaultBlockState());
            }
        }
        ball(level, origin.above(height), 2 + random.nextInt(2), 1, random);
        for (int i = 0; i < 6; i++) {
            BlockPos at = origin.offset(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
            if (soil(level, at.below()) && level.getBlockState(at).isAir()) {
                level.setBlock(at.below(), Blocks.MOSS_BLOCK.defaultBlockState(), 2);
                if (random.nextBoolean()) {
                    set(level, at, Blocks.MOSS_CARPET.defaultBlockState());
                }
            }
        }
        hangVines(level, origin.above(height - 1), 3, random, 2);
        return true;
    }

    /** Упавший ствол: лежит на земле, сверху мох, трутовик, бромелия. */
    public static boolean fallenLog(LevelAccessor level, BlockPos origin, RandomSource random) {
        Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int length = 4 + random.nextInt(4);
        BlockPos pos = origin;
        for (int i = 0; i < length; i++) {
            if (!open(level, pos) || !solidFloor(level, pos.below())) {
                if (i < 3) {
                    return false;
                }
                length = i;
                break;
            }
            pos = pos.relative(d);
        }
        BlockState log = Blocks.JUNGLE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, d.getAxis());
        pos = origin;
        for (int i = 0; i < length; i++) {
            level.setBlock(pos, log, 2);
            BlockPos top = pos.above();
            if (level.getBlockState(top).isAir()) {
                int roll = random.nextInt(10);
                if (roll < 4) {
                    level.setBlock(top, Blocks.MOSS_CARPET.defaultBlockState(), 2);
                } else if (roll < 6) {
                    level.setBlock(top, CanopyBlocks.LOG_FUNGUS.get().defaultBlockState(), 2);
                } else if (roll == 6) {
                    level.setBlock(top, CanopyBlocks.BROMELIAD.get().defaultBlockState(), 2);
                }
            }
            pos = pos.relative(d);
        }
        BlockPos stump = origin.relative(d.getOpposite());
        if (open(level, stump) && solidFloor(level, stump.below())) {
            level.setBlock(stump, Blocks.JUNGLE_LOG.defaultBlockState(), 2);
            if (open(level, stump.above())) {
                level.setBlock(stump.above(), Blocks.JUNGLE_LOG.defaultBlockState(), 2);
            }
        }
        return true;
    }

    /** Роща: несколько деревьев полога и подроста в одном месте, чтобы лес был густым, а не ровным. */
    public static boolean stand(LevelAccessor level, BlockPos origin, RandomSource random) {
        int grown = 0;
        int count = 3 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            BlockPos at = surface(level, origin.offset(random.nextInt(11) - 5, 0, random.nextInt(11) - 5));
            if (at == null) {
                continue;
            }
            boolean ok = random.nextInt(3) == 0 ? understory(level, at, random) : canopyTree(level, at, random);
            if (ok) {
                grown++;
            }
        }
        return grown > 0;
    }

    // ---- кисти ----

    private static void trunk(LevelAccessor level, BlockPos origin, int height) {
        BlockState log = Blocks.JUNGLE_LOG.defaultBlockState();
        for (int y = 0; y < height; y++) {
            level.setBlock(origin.above(y), log, 2);
        }
    }

    /** Контрфорс: ступенька брёвен от ствола наружу, выше у ствола. */
    private static void buttress(LevelAccessor level, BlockPos origin, Direction d, int reach) {
        BlockState log = Blocks.JUNGLE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, d.getAxis());
        for (int i = 1; i <= reach; i++) {
            BlockPos at = origin.relative(d, i);
            int up = reach - i;
            for (int y = 0; y <= up; y++) {
                BlockPos p = at.above(y);
                if (open(level, p) && level.getFluidState(p).isEmpty()) {
                    level.setBlock(p, y == up && i < reach ? log : Blocks.JUNGLE_LOG.defaultBlockState(), 2);
                }
            }
        }
    }

    /** Ветвь: горизонтальные брёвна с нужной осью, иногда с подъёмом на один блок. Возвращает конец. */
    private static BlockPos branch(LevelAccessor level, BlockPos from, Direction d, int length, boolean rise) {
        BlockState log = Blocks.JUNGLE_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, d.getAxis());
        BlockPos pos = from;
        for (int i = 1; i <= length; i++) {
            pos = pos.relative(d);
            if (rise && i == length) {
                pos = pos.above();
                set(level, pos, Blocks.JUNGLE_LOG.defaultBlockState());
            } else {
                set(level, pos, log);
            }
        }
        return pos;
    }

    /** Лист пальмы: полоса листвы наружу, в конце опускается. */
    private static void frond(LevelAccessor level, BlockPos from, Direction d, int length) {
        BlockPos pos = from;
        for (int i = 1; i <= length; i++) {
            pos = pos.relative(d);
            if (i == length) {
                pos = pos.below();
            }
            set(level, pos, LEAVES);
        }
        set(level, pos.below(), LEAVES);
    }

    /** Сплюснутый шар листвы. */
    private static void ball(LevelAccessor level, BlockPos center, int radius, int up, RandomSource random) {
        for (int dy = -1; dy <= up; dy++) {
            int r = dy == -1 || dy == up ? radius - 1 : radius;
            if (r < 0) {
                continue;
            }
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    int dist = dx * dx + dz * dz;
                    if (dist > r * r + 1 || (dist >= r * r - 1 && random.nextInt(3) == 0)) {
                        continue;
                    }
                    set(level, center.offset(dx, dy, dz), LEAVES);
                }
            }
        }
    }

    /**
     * Ванильные лозы с края кроны. Верхняя держится за бок листвы, нижние наследуют эту грань от лозы над собой —
     * так ваниль и держит висящие плети, иначе цепочка рассыпается при первом обновлении.
     */
    private static void hangVines(LevelAccessor level, BlockPos crown, int radius, RandomSource random, int tries) {
        for (int i = 0; i < tries; i++) {
            BlockPos leaf = crown.offset(random.nextInt(radius * 2 + 1) - radius, -1, random.nextInt(radius * 2 + 1) - radius);
            if (!level.getBlockState(leaf).is(BlockTags.LEAVES)) {
                continue;
            }
            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos at = leaf.relative(d);
            if (!level.getBlockState(at).isAir()) {
                continue;
            }
            BlockState vine = Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(d.getOpposite()), true);
            int len = 2 + random.nextInt(4);
            for (int k = 0; k < len && level.getBlockState(at).isAir(); k++) {
                level.setBlock(at, vine, 2);
                at = at.below();
            }
        }
    }

    /** Лианы-водоносы под кроной. */
    private static void hangLianas(LevelAccessor level, BlockPos crown, int radius, RandomSource random, int tries) {
        BlockState liana = CanopyBlocks.WATER_LIANA.get().defaultBlockState();
        for (int i = 0; i < tries; i++) {
            BlockPos at = crown.offset(random.nextInt(radius * 2 + 1) - radius, -1, random.nextInt(radius * 2 + 1) - radius);
            BlockPos below = at.below();
            BlockState anchor = level.getBlockState(at);
            if (!(anchor.is(BlockTags.LEAVES) || anchor.is(BlockTags.LOGS)) || !level.getBlockState(below).isAir()) {
                continue;
            }
            int len = 3 + random.nextInt(4);
            for (int k = 0; k < len && level.getBlockState(below).isAir(); k++) {
                level.setBlock(below, liana, 2);
                below = below.below();
            }
        }
    }

    private static void plant(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos).isAir()) {
            level.setBlock(pos, state, 2);
        }
    }

    private static void waterloggable(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockState at = level.getBlockState(pos);
        if (!at.isAir() && !at.getFluidState().is(FluidTags.WATER) && !at.is(BlockTags.REPLACEABLE)) {
            return;
        }
        boolean water = at.getFluidState().is(FluidTags.WATER);
        level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, water), 2);
    }

    /** Листву ставим только в пустоту: брёвна и чужие кроны не трогаем. */
    private static void set(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockState at = level.getBlockState(pos);
        if (at.isAir() || (at.is(BlockTags.REPLACEABLE) && at.getFluidState().isEmpty())) {
            level.setBlock(pos, state, 2);
        }
    }

    // ---- проверки ----

    private static boolean fits(LevelAccessor level, BlockPos origin, int height) {
        if (origin.getY() + height >= level.getMaxBuildHeight()) {
            return false;
        }
        for (int y = 0; y < height; y++) {
            if (!open(level, origin.above(y))) {
                return false;
            }
        }
        return true;
    }

    static boolean soil(LevelAccessor level, BlockPos pos) {
        BlockState s = level.getBlockState(pos);
        return s.is(BlockTags.DIRT) || s.is(Blocks.MUD) || s.is(Blocks.CLAY) || s.is(Blocks.MOSS_BLOCK);
    }

    private static boolean solidFloor(LevelAccessor level, BlockPos pos) {
        BlockState s = level.getBlockState(pos);
        return soil(level, pos) || s.is(BlockTags.BASE_STONE_OVERWORLD) || s.is(Blocks.GRAVEL) || s.is(Blocks.SAND);
    }

    static boolean open(LevelAccessor level, BlockPos pos) {
        BlockState s = level.getBlockState(pos);
        return s.isAir()
                || s.is(BlockTags.LEAVES)
                || s.is(BlockTags.REPLACEABLE)
                || s.is(Blocks.VINE)
                || s.is(CanopyBlocks.TARO.get())
                || s.is(CanopyBlocks.BITTER_HERB.get())
                || s.is(CanopyBlocks.THORNY_BRUSH.get())
                || s.is(CanopyBlocks.AIR_PLANT.get())
                || s.is(CanopyBlocks.HELICONIA.get())
                || s.is(CanopyBlocks.GINGER_LILY.get())
                || s.is(CanopyBlocks.BROMELIAD.get())
                || s.is(CanopyBlocks.LOG_FUNGUS.get());
    }

    /** Ищет уровень почвы под точкой или чуть выше. */
    private static BlockPos surface(LevelAccessor level, BlockPos start) {
        BlockPos pos = start.above(2);
        for (int i = 0; i < 10; i++) {
            if (soil(level, pos.below()) && open(level, pos)) {
                return pos;
            }
            pos = pos.below();
        }
        return null;
    }
}
