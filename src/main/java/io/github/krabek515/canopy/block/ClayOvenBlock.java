package io.github.krabek515.canopy.block;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.item.CampfireCooking;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Глиняная печь-горшок. Складывают в неё два-три съедобных корня или куска мяса,
 * под ним разводят огонь — и через минуту внутри густая похлёбка.
 * Медленнее костра, зато кормит как следует.
 */
public class ClayOvenBlock extends Block {
    public static final TagKey<Item> INGREDIENT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "stew_ingredients"));
    public static final IntegerProperty PORTIONS = IntegerProperty.create("portions", 0, 3);
    public static final BooleanProperty READY = BooleanProperty.create("ready");
    private static final int COOK_TICKS = 20 * 45;
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 15, 15);

    public ClayOvenBlock(Properties properties) {
        super(properties.randomTicks());
        this.registerDefaultState(this.defaultBlockState().setValue(PORTIONS, 0).setValue(READY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PORTIONS, READY);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (state.getValue(READY)) {
            if (!level.isClientSide) {
                ItemStack stew = new ItemStack(CanopyItems.STEW.get());
                if (!player.getInventory().add(stew)) {
                    player.drop(stew, false);
                }
                level.setBlock(pos, state.setValue(READY, false).setValue(PORTIONS, 0), 2);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.7f, 0.8f);
                if (player instanceof ServerPlayer server) {
                    JournalLearning.note(server, Journal.CRAFT_STEW);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.is(INGREDIENT) && state.getValue(PORTIONS) < 3) {
            if (!level.isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                int portions = state.getValue(PORTIONS) + 1;
                level.setBlock(pos, state.setValue(PORTIONS, portions), 2);
                level.playSound(null, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.4f, 1.4f);
                if (portions >= 2) {
                    if (fireBelow(level, pos)) {
                        level.scheduleTick(pos, this, COOK_TICKS);
                    } else {
                        player.displayClientMessage(Component.translatable("message.canopy.oven_need_fire"), true);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (state.getValue(PORTIONS) > 0 && !stack.is(INGREDIENT)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(
                        state.getValue(PORTIONS) < 2 ? "message.canopy.oven_empty" : "message.canopy.oven_cooking"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    /** Через COOK_TICKS похлёбка готова — но только если огонь под печью не погас. */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(READY) || state.getValue(PORTIONS) < 2) {
            return;
        }
        if (fireBelow(level, pos)) {
            level.setBlock(pos, state.setValue(READY, true), 2);
            level.playSound(null, pos, SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.6f, 0.8f);
        }
    }

    /** Если огонь гасили и разводили заново, случайный тик в конце концов доведёт варево до готовности. */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(READY) && state.getValue(PORTIONS) >= 2 && fireBelow(level, pos)) {
            level.scheduleTick(pos, this, 20 * 10);
        }
    }

    private static boolean fireBelow(Level level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return CampfireCooking.isLit(below) || below.is(Blocks.FIRE) || below.is(Blocks.SOUL_FIRE);
    }
}
