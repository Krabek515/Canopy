package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Нарост на стволе гевеи. Стоит вместо бревна в нижней части ствола.
 * Стамеской с него снимают каучуковый сок; потом сок заполняется снова.
 */
public class RubberKnotBlock extends RotatedPillarBlock {
    public static final BooleanProperty TAPPED = BooleanProperty.create("tapped");

    public RubberKnotBlock(Properties properties) {
        super(properties.randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(TAPPED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TAPPED);
    }

    /** Сок течёт медленно: случайный тик в среднем раз в минуту, одна из сорока попыток его восстанавливает. */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(TAPPED) && random.nextInt(40) == 0) {
            level.setBlock(pos, state.setValue(TAPPED, false), 2);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(CanopyItems.GOUGE.get())) {
            return InteractionResult.PASS;
        }
        if (state.getValue(TAPPED)) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            player.displayClientMessage(Component.translatable("message.canopy.rubber_dry"), true);
            return InteractionResult.CONSUME;
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(TAPPED, true), 2);
            Block.popResource(level, pos.above(), new ItemStack(CanopyItems.RUBBER_SAP.get(), 1 + level.random.nextInt(2)));
            level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.8f, 0.6f);
            if (!player.getAbilities().instabuild) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            if (player instanceof ServerPlayer server) {
                JournalLearning.note(server, Journal.CRAFT_RUBBER);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
