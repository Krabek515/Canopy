package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.block.CanopyBlocks;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class ExpeditionCamp {
    private ExpeditionCamp() {
    }

    public static boolean grow(LevelAccessor level, BlockPos origin) {
        if (!site(level, origin)) {
            return false;
        }
        BlockState cold = Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, false);
        level.setBlock(origin, cold, 2);
        level.setBlock(origin.offset(-1, 0, 0), Blocks.BAMBOO_FENCE.defaultBlockState(), 2);
        level.setBlock(origin.offset(0, 0, -1), Blocks.BAMBOO_FENCE.defaultBlockState(), 2);
        level.setBlock(origin.offset(1, 0, 0), CanopyBlocks.EXPEDITION_CACHE.get().defaultBlockState(), 2);
        return true;
    }

    public static void open(ServerLevel level, BlockPos pos, ServerPlayer player) {
        if (!level.getBlockState(pos).is(CanopyBlocks.EXPEDITION_CACHE.get())) {
            return;
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        give(player, new ItemStack(CanopyItems.GOUGE.get()));
        give(player, new ItemStack(CanopyItems.FIBER_TUNIC.get()));
        give(player, new ItemStack(CanopyItems.NUGGET.get()));
        JournalLearning.note(player, Journal.CAMP);
        JournalLearning.note(player, Journal.GOUGE);
        JournalLearning.note(player, Journal.VEIN);
        CanopyAdvancements.award(player, "left_behind");
        level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 0.8f, 0.9f);
        player.displayClientMessage(Component.translatable("message.canopy.camp_open"), true);
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static boolean site(LevelAccessor level, BlockPos origin) {
        BlockPos[] spots = {origin, origin.offset(1, 0, 0), origin.offset(-1, 0, 0), origin.offset(0, 0, -1)};
        for (BlockPos spot : spots) {
            if (!open(level, spot) || !open(level, spot.above()) || !level.getFluidState(spot).isEmpty()) {
                return false;
            }
            BlockState ground = level.getBlockState(spot.below());
            if (!ground.is(BlockTags.DIRT) && !ground.is(Blocks.MUD) && !ground.is(Blocks.CLAY)) {
                return false;
            }
        }
        return true;
    }

    private static boolean open(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.LEAVES) || state.is(BlockTags.REPLACEABLE)
                || state.is(CanopyBlocks.THORNY_BRUSH.get())
                || state.is(CanopyBlocks.TARO.get())
                || state.is(CanopyBlocks.BITTER_HERB.get());
    }
}
