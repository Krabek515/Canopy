package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class CampfireCooking {
    private CampfireCooking() {
    }

    public static boolean isLit(BlockState state) {
        return state.is(BlockTags.CAMPFIRES)
                && state.hasProperty(BlockStateProperties.LIT)
                && state.getValue(BlockStateProperties.LIT);
    }

    public static InteractionResult cook(UseOnContext context, Item result) {
        return cook(context, result, true);
    }

    public static InteractionResult cook(UseOnContext context, Item result, boolean meal) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        if (!level.isClientSide) {
            ItemStack cooked = new ItemStack(result);
            if (!player.getInventory().add(cooked)) {
                player.drop(cooked, false);
            }
            level.playSound(null, context.getClickedPos(), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.7f, 1f);
            if (player instanceof ServerPlayer server) {
                if (meal) {
                    server.displayClientMessage(Component.translatable("message.canopy.cooked"), true);
                    CanopyAdvancements.award(server, "cooked_meal");
                    JournalLearning.note(server, Journal.CRAFT_COOKED);
                } else {
                    server.displayClientMessage(Component.translatable("message.canopy.brewed"), true);
                    JournalLearning.note(server, Journal.ANTIDOTE);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
