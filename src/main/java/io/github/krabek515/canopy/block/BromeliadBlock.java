package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.item.FlaskItem;
import io.github.krabek515.canopy.item.FlaskWater;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/** Эпифит на земле, бревне или ветке. В розетке копится дождевая вода: пустая фляга забирает её как свежую. */
public class BromeliadBlock extends BushBlock {
    public static final BooleanProperty FILLED = BlockStateProperties.ENABLED;

    public BromeliadBlock(Properties properties) {
        super(properties.randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(FILLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FILLED);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.is(Blocks.MUD) || state.is(Blocks.MOSS_BLOCK)
                || state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(FILLED) && level.isRainingAt(pos.above())) {
            level.setBlock(pos, state.setValue(FILLED, true), 2);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!state.getValue(FILLED) || !stack.is(CanopyItems.FLASK.get()) || FlaskItem.waterOf(stack) != FlaskWater.EMPTY) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            FlaskItem.setWater(stack, FlaskWater.FRESH);
            level.setBlock(pos, state.setValue(FILLED, false), 2);
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.5f, 1.2f);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
