package io.github.krabek515.canopy.journal;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.block.CanopyBlocks;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.entity.Agouti;
import io.github.krabek515.canopy.entity.Caiman;
import io.github.krabek515.canopy.entity.Capybara;
import io.github.krabek515.canopy.entity.Gorilla;
import io.github.krabek515.canopy.entity.Tapir;
import io.github.krabek515.canopy.entity.Toucan;
import io.github.krabek515.canopy.entity.Jaguar;
import io.github.krabek515.canopy.entity.Monkey;
import io.github.krabek515.canopy.entity.Snake;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.item.DiaryItem;
import io.github.krabek515.canopy.network.CanopyNetwork;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)

public final class JournalLearning {
    private JournalLearning() {
    }

    @SubscribeEvent
    public static void onEat(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack eaten = event.getItem();
        if (eaten.is(CanopyItems.TARO_ROOT.get())) {
            note(player, Journal.TARO_SEEN);
            note(player, Journal.TARO_RAW);
        } else if (eaten.is(CanopyItems.COOKED_TARO.get())) {
            note(player, Journal.TARO_SEEN);
            note(player, Journal.TARO_COOKED);
            note(player, Journal.CRAFT_COOKED);
        } else if (eaten.is(CanopyItems.COOKED_MEAT.get())) {
            note(player, Journal.CRAFT_COOKED);
        } else if (eaten.is(CanopyItems.STEW.get())) {
            note(player, Journal.CRAFT_STEW);
        }
    }

    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack crafted = event.getCrafting();
        if (crafted.is(CanopyItems.FLASK.get())) {
            note(player, Journal.CRAFT_FLASK);
        } else if (crafted.is(CanopyItems.STONE_MACHETE.get())) {
            note(player, Journal.CRAFT_MACHETE);
        } else if (crafted.is(CanopyItems.FIBER_HOOD.get()) || crafted.is(CanopyItems.FIBER_TUNIC.get())
                || crafted.is(CanopyItems.FIBER_LEGGINGS.get()) || crafted.is(CanopyItems.FIBER_SANDALS.get())) {
            note(player, Journal.CRAFT_FIBER);
        } else if (crafted.is(CanopyItems.HIDE_HOOD.get()) || crafted.is(CanopyItems.HIDE_COAT.get())
                || crafted.is(CanopyItems.HIDE_LEGGINGS.get()) || crafted.is(CanopyItems.HIDE_BOOTS.get())) {
            note(player, Journal.CRAFT_HIDE);
        } else if (crafted.is(CanopyItems.BANDAGE.get())) {
            note(player, Journal.CRAFT_BANDAGE);
        } else if (crafted.is(CanopyItems.ROPE.get())) {
            note(player, Journal.ROPE);
        } else if (crafted.is(CanopyItems.GOUGE.get())) {
            note(player, Journal.GOUGE);
        } else if (crafted.is(CanopyItems.METAL_MACHETE.get())) {
            note(player, Journal.METAL);
        } else if (crafted.is(CanopyItems.RESPIRATOR.get())) {
            note(player, Journal.RESPIRATOR);
        } else if (crafted.is(CanopyItems.BAD_BREW.get())) {
            note(player, Journal.BAD_BREW);
        } else if (crafted.is(CanopyItems.BONE_MACHETE.get())) {
            note(player, Journal.CRAFT_BONE);
        } else if (crafted.getItem() instanceof io.github.krabek515.canopy.item.ClothingItem
                && io.github.krabek515.canopy.item.RubberSapItem.waxed(crafted)) {
            note(player, Journal.CRAFT_RUBBER);
        }
    }

    public static void note(ServerPlayer player, String fact) {
        player.getCapability(CanopyCapabilities.JOURNAL).ifPresent(journal -> journal.learn(fact));
    }

    public static void open(ServerPlayer player) {
        player.getCapability(CanopyCapabilities.JOURNAL).ifPresent(journal -> {
            journal.clearDirty();
            CanopyNetwork.sendJournal(player, journal, true);
            CanopyAdvancements.award(player, "field_notes");
        });
    }

    public static void sync(ServerPlayer player) {
        player.getCapability(CanopyCapabilities.JOURNAL).ifPresent(journal -> {
            if (!journal.isDirty()) {
                return;
            }
            journal.clearDirty();
            CanopyNetwork.sendJournal(player, journal, false);
        });
    }

    public static void observe(ServerPlayer player) {
        DiaryItem.ensure(player);
        player.getCapability(CanopyCapabilities.JOURNAL).ifPresent(journal -> {
            player.level().getBiome(player.blockPosition()).unwrapKey().ifPresent(key -> {
                if (Canopy.MOD_ID.equals(key.location().getNamespace())) {
                    journal.learn("place_" + key.location().getPath());
                }
            });
            if (holds(player, CanopyItems.TARO_ROOT) || near(player, CanopyBlocks.TARO.get())) {
                journal.learn(Journal.TARO_SEEN);
            }
            if (holds(player, CanopyItems.BITTER_HERB) || near(player, CanopyBlocks.BITTER_HERB.get())) {
                journal.learn(Journal.BITTER_SEEN);
            }
            AABB box = player.getBoundingBox().inflate(16);
            if (sees(player, Jaguar.class, box)) {
                journal.learn(Journal.JAGUAR);
            }
            if (sees(player, Agouti.class, box)) {
                journal.learn(Journal.AGOUTI);
            }
            if (sees(player, Snake.class, box)) {
                journal.learn(Journal.SNAKE);
            }
            if (sees(player, Monkey.class, box)) {
                journal.learn(Journal.MONKEY);
            }
            if (sees(player, Capybara.class, box)) {
                journal.learn(Journal.CAPYBARA);
            }
            if (sees(player, Tapir.class, box)) {
                journal.learn(Journal.TAPIR);
            }
            if (sees(player, Caiman.class, box)) {
                journal.learn(Journal.CAIMAN);
            }
            if (sees(player, Gorilla.class, box)) {
                journal.learn(Journal.GORILLA);
            }
            if (sees(player, Toucan.class, box)) {
                journal.learn(Journal.TOUCAN);
            }
            if (holds(player, CanopyItems.FLASK)) {
                journal.learn(Journal.CRAFT_FLASK);
            }
            if (holds(player, CanopyItems.STONE_MACHETE)) {
                journal.learn(Journal.CRAFT_MACHETE);
            }
            if (holds(player, CanopyItems.COOKED_TARO) || holds(player, CanopyItems.COOKED_MEAT)) {
                journal.learn(Journal.CRAFT_COOKED);
            }
            if (holds(player, CanopyItems.FIBER_HOOD) || holds(player, CanopyItems.FIBER_TUNIC)
                    || holds(player, CanopyItems.FIBER_LEGGINGS) || holds(player, CanopyItems.FIBER_SANDALS)) {
                journal.learn(Journal.CRAFT_FIBER);
            }
            if (holds(player, CanopyItems.HIDE_HOOD) || holds(player, CanopyItems.HIDE_COAT)
                    || holds(player, CanopyItems.HIDE_LEGGINGS) || holds(player, CanopyItems.HIDE_BOOTS)) {
                journal.learn(Journal.CRAFT_HIDE);
            }
            if (holds(player, CanopyItems.BANDAGE)) {
                journal.learn(Journal.CRAFT_BANDAGE);
            }
            if (holds(player, CanopyItems.SILK) || near(player, CanopyBlocks.SILK_POD.get())) {
                journal.learn(Journal.CANOPY);
            }
            if (holds(player, CanopyItems.ROPE)) {
                journal.learn(Journal.ROPE);
            }
            if (holds(player, CanopyItems.NUGGET) || near(player, CanopyBlocks.EXPOSED_VEIN.get())) {
                journal.learn(Journal.VEIN);
            }
            if (holds(player, CanopyItems.GOUGE)) {
                journal.learn(Journal.GOUGE);
            }
            if (holds(player, CanopyItems.METAL_MACHETE)) {
                journal.learn(Journal.METAL);
            }
            if (near(player, CanopyBlocks.GAS_SEEP.get())) {
                journal.learn(Journal.GAS);
            }
            if (near(player, CanopyBlocks.AIR_PLANT.get())) {
                journal.learn(Journal.AIR_PLANT);
            }
            if (holds(player, CanopyItems.BROMELIAD) || near(player, CanopyBlocks.BROMELIAD.get())) {
                journal.learn(Journal.PLANT_BROMELIAD);
            }
            if (holds(player, CanopyItems.HELICONIA) || near(player, CanopyBlocks.HELICONIA.get())) {
                journal.learn(Journal.PLANT_HELICONIA);
            }
            if (holds(player, CanopyItems.GINGER_LILY) || near(player, CanopyBlocks.GINGER_LILY.get())) {
                journal.learn(Journal.PLANT_GINGER);
            }
            if (holds(player, CanopyItems.LOG_FUNGUS) || near(player, CanopyBlocks.LOG_FUNGUS.get())) {
                journal.learn(Journal.PLANT_FUNGUS);
            }
            if (holds(player, CanopyItems.BANANA) || near(player, CanopyBlocks.BANANA_BUNCH.get())) {
                journal.learn("plant_banana");
            }
            if (holds(player, CanopyItems.ORCHID) || near(player, CanopyBlocks.ORCHID.get())) {
                journal.learn("plant_orchid");
            }
            if (holds(player, CanopyItems.RESPIRATOR)) {
                journal.learn(Journal.RESPIRATOR);
            }
            if (holds(player, CanopyItems.ANTIDOTE)) {
                journal.learn(Journal.ANTIDOTE);
            }
            if (holds(player, CanopyItems.BAD_BREW)) {
                journal.learn(Journal.BAD_BREW);
            }
            if (holds(player, CanopyItems.BONE_MACHETE)) {
                journal.learn(Journal.CRAFT_BONE);
            }
            if (holds(player, CanopyItems.RUBBER_SAP) || near(player, CanopyBlocks.RUBBER_KNOT.get())) {
                journal.learn(Journal.CRAFT_RUBBER);
            }
            if (holds(player, CanopyItems.HAMMOCK) || holds(player, CanopyItems.STILT_PLATFORM)) {
                journal.learn(Journal.CAMP_BUILT);
            }
        });
    }

    private static boolean holds(Player player, RegistryObject<Item> item) {
        return player.getInventory().contains(new ItemStack(item.get()));
    }

    private static boolean near(Player player, net.minecraft.world.level.block.Block block) {
        BlockPos center = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-3, -1, -3), center.offset(3, 2, 3))) {
            if (player.level().getBlockState(pos).is(block)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sees(ServerPlayer player, Class<? extends Entity> type, AABB box) {
        return !player.level().getEntitiesOfClass(type, box, entity -> player.hasLineOfSight(entity)).isEmpty();
    }
}
