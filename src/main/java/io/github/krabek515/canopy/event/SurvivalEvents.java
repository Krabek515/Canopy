package io.github.krabek515.canopy.event;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.body.BodySimulation;
import io.github.krabek515.canopy.body.FoodSpoilage;
import io.github.krabek515.canopy.body.HandWork;
import io.github.krabek515.canopy.body.HeatSources;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.config.CanopyConfig;
import io.github.krabek515.canopy.world.ForestDress;
import io.github.krabek515.canopy.world.ForestSound;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.item.DiaryItem;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.network.CanopyNetwork;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
public final class SurvivalEvents {
    private static final Set<Long> DRESSED = ConcurrentHashMap.newKeySet();

    private SurvivalEvents() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event) {
        // ProtoChunk ещё десериализуется — биомов в нём нет, getNoiseBiome упадёт.
        // Свежие чанки одевает UndergrowthFeature на генерации; здесь доодеваем только старые полные чанки.
        if (!(event.getChunk() instanceof net.minecraft.world.level.chunk.LevelChunk)) {
            return;
        }
        long key = event.getChunk().getPos().toLong();
        if (event.getData().getInt("CanopyDress") >= ForestDress.VERSION) {
            DRESSED.add(key);
            return;
        }
        if (!DRESSED.add(key)) {
            return;
        }
        ForestDress.dress(event.getChunk());
        event.getData().putInt("CanopyDress", ForestDress.VERSION);
    }

    @SubscribeEvent
    public static void onChunkSave(ChunkDataEvent.Save event) {
        ChunkAccess chunk = event.getChunk();
        if (DRESSED.contains(chunk.getPos().toLong()) || event.getData().getInt("CanopyDress") >= ForestDress.VERSION) {
            event.getData().putInt("CanopyDress", ForestDress.VERSION);
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        HandWork.slowInThicket(event.player);
        if (!(event.player instanceof ServerPlayer player) || player.isSpectator()) {
            return;
        }
        if (!player.isCreative()) {
            tickBody(player);
        }
        if (player.tickCount % 20 == 0) {
            JournalLearning.observe(player);
            ForestSound.tick(player);
        }
        if (player.tickCount % 5 == 0) {
            JournalLearning.sync(player);
            player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
                if (stats.isDirty()) {
                    stats.clearDirty();
                    CanopyNetwork.send(player);
                }
            });
        }
    }

    private static void tickBody(ServerPlayer player) {
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
            if (player.tickCount % 20 == 0) {
                stats.setNearHeat(HeatSources.near(player, 5));
                BodySimulation.tickSecond(player, stats);
                FoodSpoilage.tickSecond(player, stats);
            }
            if (player.tickCount % 5 == 0 && stats.isDirty()) {
                stats.clearDirty();
                CanopyNetwork.send(player);
            }
        });
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> HandWork.adjustBreak(event, stats));
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        HandWork.afterBreak(event);
    }

    @SubscribeEvent
    public static void onSmelted(PlayerEvent.ItemSmeltedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack stack = event.getSmelting();
        if (stack.is(CanopyItems.COOKED_TARO.get()) || stack.is(CanopyItems.COOKED_MEAT.get())) {
            CanopyAdvancements.award(player, "cooked_meal");
            JournalLearning.note(player, io.github.krabek515.canopy.journal.Journal.CRAFT_COOKED);
        }
    }

    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            event.getDrops().removeIf(dropped -> dropped.getItem().is(CanopyItems.DIARY.get()));
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(CanopyCapabilities.SURVIVAL).ifPresent(oldStats ->
                event.getEntity().getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> stats.copyFrom(oldStats)));
        event.getOriginal().getCapability(CanopyCapabilities.JOURNAL).ifPresent(oldJournal ->
                event.getEntity().getCapability(CanopyCapabilities.JOURNAL).ifPresent(journal -> journal.copyFrom(oldJournal)));
        event.getOriginal().invalidateCaps();
        long nextRaid = event.getOriginal().getPersistentData().getLong("CanopyRaidNext");
        if (nextRaid != 0L) {
            event.getEntity().getPersistentData().putLong("CanopyRaidNext", nextRaid);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DiaryItem.ensure(player);
            CanopyNetwork.send(player);
            JournalLearning.sync(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DiaryItem.ensure(player);
            CanopyNetwork.send(player);
            JournalLearning.sync(player);
        }
    }

    @SubscribeEvent
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CanopyNetwork.send(player);
            JournalLearning.sync(player);
        }
    }
}
