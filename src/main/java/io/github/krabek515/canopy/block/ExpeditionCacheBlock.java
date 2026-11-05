package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.world.ExpeditionCamp;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ExpeditionCacheBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 4, 14);

    public ExpeditionCacheBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel server) {
            ExpeditionCamp.open(server, pos, serverPlayer);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!player.isSpectator() && player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel server) {
            ExpeditionCamp.open(server, pos, serverPlayer);
        }
        super.playerWillDestroy(level, pos, state, player);
    }
}
