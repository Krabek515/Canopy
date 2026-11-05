package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.journal.JournalLearning;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiaryItem extends Item {
    public DiaryItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void ensure(ServerPlayer player) {
        if (carrying(player)) {
            return;
        }
        ItemStack book = new ItemStack(CanopyItems.DIARY.get());
        if (!player.getInventory().add(book)) {
            ItemStack previous = player.getInventory().getItem(0);
            player.getInventory().setItem(0, book);
            if (!previous.isEmpty()) {
                player.drop(previous, false);
            }
        }
    }

    public static boolean carrying(Player player) {
        if (player.getOffhandItem().is(CanopyItems.DIARY.get())) {
            return true;
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(CanopyItems.DIARY.get())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer server) {
            JournalLearning.open(server);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, Player player) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.canopy.journal.hint").withStyle(ChatFormatting.GRAY));
    }
}
