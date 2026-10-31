package io.github.krabek515.canopy.item;

import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.network.CanopyNetwork;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class FlaskItem extends Item {
    public static final String TAG_WATER = "Water";

    public FlaskItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static FlaskWater waterOf(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return FlaskWater.EMPTY;
        }
        return FlaskWater.byName(tag.getString(TAG_WATER));
    }

    public static void setWater(ItemStack stack, FlaskWater water) {
        if (water == FlaskWater.EMPTY) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(TAG_WATER);
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }
            return;
        }
        stack.getOrCreateTag().putString(TAG_WATER, water.name());
    }

    public static ItemStack withWater(FlaskWater water) {
        ItemStack stack = new ItemStack(CanopyItems.FLASK.get());
        setWater(stack, water);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(switch (waterOf(stack)) {
            case MURKY -> "item.canopy.bamboo_flask.murky";
            case CLEAN -> "item.canopy.bamboo_flask.clean";
            case EMPTY -> "item.canopy.bamboo_flask";
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String key = switch (waterOf(stack)) {
            case MURKY -> "item.canopy.bamboo_flask.hint.murky";
            case CLEAN -> "item.canopy.bamboo_flask.hint.clean";
            case EMPTY -> "item.canopy.bamboo_flask.hint.empty";
        };
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockState state = level.getBlockState(context.getClickedPos());
        ItemStack stack = context.getItemInHand();
        FlaskWater water = waterOf(stack);
        if (water == FlaskWater.MURKY && isLitHeat(state)) {
            setWater(stack, FlaskWater.CLEAN);
            if (!level.isClientSide) {
                level.playSound(null, context.getClickedPos(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.35f, 1.4f);
                if (context.getPlayer() instanceof ServerPlayer player) {
                    player.displayClientMessage(Component.translatable("message.canopy.boiled"), true);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (water == FlaskWater.EMPTY && level.getFluidState(context.getClickedPos()).is(FluidTags.WATER)) {
            fill(level, context.getPlayer(), stack, context.getClickedPos());
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FlaskWater water = waterOf(stack);
        if (water == FlaskWater.EMPTY) {
            HitResult hit = player.pick(5.0, 0f, true);
            if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult blockHit
                    && level.getFluidState(blockHit.getBlockPos()).is(FluidTags.WATER)) {
                fill(level, player, stack, blockHit.getBlockPos());
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
            return InteractionResultHolder.pass(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return waterOf(stack) == FlaskWater.EMPTY ? 0 : 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return waterOf(stack) == FlaskWater.EMPTY ? UseAnim.NONE : UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        FlaskWater water = waterOf(stack);
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
                if (water == FlaskWater.MURKY) {
                    stats.drink(3f);
                    player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0));
                } else if (water == FlaskWater.CLEAN) {
                    stats.drink(10f);
                    CanopyAdvancements.award(player, "clean_water");
                }
            });
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.55f, 1f);
            CanopyNetwork.send(player);
        }
        if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
            setWater(stack, FlaskWater.EMPTY);
        }
        return stack;
    }

    private static boolean isLitHeat(BlockState state) {
        return state.is(BlockTags.CAMPFIRES)
                && state.hasProperty(BlockStateProperties.LIT)
                && state.getValue(BlockStateProperties.LIT);
    }

    private static void fill(Level level, @Nullable Player player, ItemStack stack, net.minecraft.core.BlockPos pos) {
        setWater(stack, FlaskWater.MURKY);
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 0.5f, 1f);
        }
    }
}
