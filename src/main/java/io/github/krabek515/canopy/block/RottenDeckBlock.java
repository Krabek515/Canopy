package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.body.CanopyDamage;
import io.github.krabek515.canopy.body.WoundCare;
import io.github.krabek515.canopy.entity.CanopyEntities;
import io.github.krabek515.canopy.entity.Snake;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class RottenDeckBlock extends Block {
    public static final EnumProperty<DeckKind> KIND = EnumProperty.create("kind", DeckKind.class);

    public RottenDeckBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(KIND, DeckKind.BOG));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(KIND);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player) || player.isSpectator() || player.isShiftKeyDown()) {
            return;
        }
        if (level.getBlockTicks().hasScheduledTick(pos, this)) {
            return;
        }
        level.scheduleTick(pos, this, 15);
        level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.55f, 0.45f);
        JournalLearning.note(player, Journal.LIVING_FLOOR);
        player.displayClientMessage(Component.translatable("message.canopy.deck_creak"), true);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.is(this) || !occupied(level, pos)) {
            return;
        }
        collapse(level, pos, state.getValue(KIND));
    }

    private static boolean occupied(ServerLevel level, BlockPos deck) {
        AABB box = new AABB(deck.above());
        return !level.getEntitiesOfClass(ServerPlayer.class, box, player -> !player.isSpectator() && !player.isShiftKeyDown() && player.onGround()).isEmpty();
    }

    private static void collapse(ServerLevel level, BlockPos start, DeckKind kind) {
        Set<BlockPos> deck = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        while (!queue.isEmpty() && deck.size() < 16) {
            BlockPos pos = queue.removeFirst();
            if (!level.getBlockState(pos).is(CanopyBlocks.ROTTEN_DECK.get()) || !deck.add(pos)) {
                continue;
            }
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                queue.add(pos.relative(direction));
            }
        }
        AABB fall = new AABB(start).inflate(2, 2, 2);
        for (BlockPos pos : deck) {
            level.destroyBlock(pos, false);
        }
        level.playSound(null, start, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.9f, 0.7f);
        if (kind == DeckKind.SNAKE) {
            spawnSnake(level, start);
        }
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, fall, player -> !player.isSpectator())) {
            player.displayClientMessage(Component.translatable(switch (kind) {
                case BOG -> "message.canopy.deck_bog";
                case STAKES -> "message.canopy.deck_stakes";
                case SNAKE -> "message.canopy.deck_snake";
            }), true);
            CanopyAdvancements.award(player, "gave_way");
            if (kind == DeckKind.STAKES) {
                player.hurt(CanopyDamage.source(level, CanopyDamage.THORNS), 3f);
                WoundCare.open(player);
            }
        }
    }

    private static void spawnSnake(ServerLevel level, BlockPos deck) {
        BlockPos floor = deck;
        for (int i = 0; i < 6; i++) {
            BlockPos below = floor.below();
            if (!level.getBlockState(below).isAir() && !level.getFluidState(below).isEmpty()) {
                break;
            }
            if (!level.getBlockState(below).isAir()) {
                break;
            }
            floor = below;
        }
        AABB box = new AABB(floor).inflate(2);
        if (!level.getEntitiesOfClass(Snake.class, box).isEmpty()) {
            return;
        }
        Snake snake = CanopyEntities.SNAKE.get().create(level);
        if (snake == null) {
            return;
        }
        snake.moveTo(floor.getX() + 0.5, floor.getY(), floor.getZ() + 0.5, level.random.nextFloat() * 360f, 0f);
        snake.finalizeSpawn(level, level.getCurrentDifficultyAt(floor), MobSpawnType.EVENT, null, null);
        level.addFreshEntity(snake);
    }
}
