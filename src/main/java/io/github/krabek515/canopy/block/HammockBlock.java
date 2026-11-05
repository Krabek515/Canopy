package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Гамак из шёлка и верёвки. Лежак ванильной кровати, только лёгкий:
 * края должны упираться в стволы, стену или стойки свайной платформы.
 * Сон в гамаке ставит точку возрождения — это и есть лагерь.
 */
public class HammockBlock extends BedBlock {
    public HammockBlock(Properties properties) {
        super(DyeColor.GREEN, properties);
    }

    @Override
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public net.minecraft.world.level.block.RenderShape getRenderShape(BlockState state) {
        return net.minecraft.world.level.block.RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && !anchored(state, level, pos)) {
            player.displayClientMessage(Component.translatable("message.canopy.hammock_loose"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result.consumesAction() && player instanceof ServerPlayer server) {
            JournalLearning.note(server, Journal.CAMP_BUILT);
        }
        return result;
    }

    /** Гамак висит между двумя опорами: стволом, твёрдой гранью или стойкой платформы за каждым концом. */
    private static boolean anchored(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        boolean isHead = state.getValue(PART) == BedPart.HEAD;
        BlockPos headPos = isHead ? pos : pos.relative(facing);
        BlockPos footPos = isHead ? pos.relative(facing.getOpposite()) : pos;
        return anchor(level, headPos.relative(facing)) && anchor(level, footPos.relative(facing.getOpposite()));
    }

    private static boolean anchor(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(BlockTags.LOGS) || state.is(CanopyBlocks.STILT_PLATFORM.get());
    }
}
