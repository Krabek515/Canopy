package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.body.HandWork;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.item.FlaskItem;
import io.github.krabek515.canopy.item.FlaskWater;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Водоносная лиана. Висит с листвы или ветки, по ней лезут. Пустая фляга с мачете в другой руке
 * срезает её и забирает сок: вода почти чистая, но сама лиана пропадает.
 */
public class LianaBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);

    public LianaBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(this) || above.is(BlockTags.LEAVES) || above.is(BlockTags.LOGS) || above.isFaceSturdy(level, pos.above(), Direction.DOWN);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(CanopyItems.FLASK.get()) || FlaskItem.waterOf(stack) != FlaskWater.EMPTY) {
            return InteractionResult.PASS;
        }
        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        boolean machete = HandWork.isMachete(player.getItemInHand(other));
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!machete) {
            player.displayClientMessage(Component.translatable("message.canopy.liana_needs_machete"), true);
            return InteractionResult.CONSUME;
        }
        FlaskItem.setWater(stack, FlaskWater.FRESH);
        level.destroyBlock(pos, false);
        level.playSound(null, pos, SoundEvents.BAMBOO_BREAK, SoundSource.BLOCKS, 0.7f, 0.8f);
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.5f, 1.1f);
        if (player instanceof ServerPlayer server) {
            JournalLearning.note(server, Journal.PLANT_LIANA);
        }
        return InteractionResult.CONSUME;
    }
}
