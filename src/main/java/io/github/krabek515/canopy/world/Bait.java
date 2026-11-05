package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.block.CanopyBlocks;
import io.github.krabek515.canopy.entity.CanopyEntities;
import io.github.krabek515.canopy.entity.Jaguar;
import io.github.krabek515.canopy.item.CanopyItems;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class Bait {
    private Bait() {
    }

    public static boolean place(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition().relative(player.getDirection(), 2);
        if (!level.getBlockState(pos).isAir()) {
            pos = pos.above();
        }
        if (!level.getBlockState(pos).isAir()) {
            return false;
        }
        level.setBlock(pos, CanopyBlocks.MARKED_CARGO.get().defaultBlockState(), 3);
        return true;
    }

    public static void open(ServerLevel level, BlockPos pos, ServerPlayer player) {
        if (!level.getBlockState(pos).is(CanopyBlocks.MARKED_CARGO.get())) {
            return;
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        ItemStack coat = new ItemStack(CanopyItems.HIDE_COAT.get());
        if (!player.getInventory().add(coat)) {
            player.drop(coat, false);
        }
        JournalLearning.note(player, Journal.BAIT);
        CanopyAdvancements.award(player, "from_above");
        level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.55f, 0.9f);
        BlockPos crack = pos.above(5);
        level.playSound(null, crack, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.85f, 1.35f);
        player.displayClientMessage(Component.translatable("message.canopy.bait_crack"), true);
        dropCat(level, pos, player);
    }

    private static void dropCat(ServerLevel level, BlockPos cargo, ServerPlayer player) {
        BlockPos spot = dropSpot(level, cargo);
        if (spot == null) {
            return;
        }
        Jaguar jaguar = CanopyEntities.JAGUAR.get().create(level);
        if (jaguar == null) {
            return;
        }
        double x = spot.getX() + 0.5;
        double z = spot.getZ() + 0.5;
        float yaw = (float) (Math.toDegrees(Math.atan2(player.getZ() - z, player.getX() - x)) - 90.0);
        jaguar.moveTo(x, spot.getY(), z, yaw, 0f);
        jaguar.finalizeSpawn(level, level.getCurrentDifficultyAt(spot), MobSpawnType.EVENT, null, null);
        jaguar.setPersistenceRequired();
        jaguar.setTarget(player);
        jaguar.setDeltaMovement(0.0, -0.2, 0.0);
        jaguar.hurtMarked = true;
        level.addFreshEntity(jaguar);
        jaguar.setTarget(player);
    }

    private static BlockPos dropSpot(ServerLevel level, BlockPos cargo) {
        BlockPos underLeaves = null;
        for (int dy = 3; dy <= 8; dy++) {
            BlockPos cell = cargo.above(dy);
            if (level.getBlockState(cell).isAir() && level.getBlockState(cell.above()).is(BlockTags.LEAVES)) {
                underLeaves = cell;
            }
        }
        if (underLeaves != null && clear(level, underLeaves)) {
            return underLeaves;
        }
        for (int dy = 8; dy >= 4; dy--) {
            BlockPos cell = cargo.above(dy);
            if (clear(level, cell)) {
                return cell;
            }
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos side = cargo.relative(direction).above(6);
            if (clear(level, side)) {
                return side;
            }
        }
        return null;
    }

    private static boolean clear(ServerLevel level, BlockPos pos) {
        BlockState here = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());
        return here.isAir() && here.getFluidState().isEmpty() && above.getCollisionShape(level, pos.above()).isEmpty() && above.getFluidState().isEmpty();
    }
}
