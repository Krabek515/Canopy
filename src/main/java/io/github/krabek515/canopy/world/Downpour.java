package io.github.krabek515.canopy.world;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.config.CanopyConfig;
import io.github.krabek515.canopy.entity.MonkeyRaid;
import io.github.krabek515.canopy.journal.Journal;
import io.github.krabek515.canopy.journal.JournalLearning;
import io.github.krabek515.canopy.progress.CanopyAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
public final class Downpour extends SavedData {
    private long until;
    private long nextAt;
    private boolean torchesDoused;
    private boolean wasActive;

    public Downpour() {
    }

    public static Downpour get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(Downpour::load, Downpour::new, "canopy_weather");
    }

    public static Downpour load(CompoundTag tag) {
        Downpour data = new Downpour();
        data.until = tag.getLong("Until");
        data.nextAt = tag.getLong("Next");
        data.torchesDoused = tag.getBoolean("Doused");
        return data;
    }

    public static boolean active(Level level) {
        if (level instanceof ServerLevel server) {
            return get(server).running(server.getGameTime());
        }
        return level.isThundering() && level.isRaining();
    }

    @SubscribeEvent
    public static void onLevel(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide || !(event.level instanceof ServerLevel level)) {
            return;
        }
        if (!level.dimension().equals(Level.OVERWORLD) || level.getGameTime() % 20L != 0L) {
            return;
        }
        WetSeason.tick(level);
        get(level).tick(level);
        FlashFlood.consider(level);
        if (level.getGameTime() % 100L == 0L) {
            MonkeyRaid.consider(level);
        }
    }

    public void force(ServerLevel level) {
        begin(level, level.getGameTime());
    }

    private void tick(ServerLevel level) {
        long now = level.getGameTime();
        if (running(now)) {
            this.wasActive = true;
            int left = (int) Math.max(1L, this.until - now);
            level.setWeatherParameters(0, left, true, true);
            affect(level);
            return;
        }
        if (this.wasActive) {
            this.wasActive = false;
            this.torchesDoused = false;
            level.setWeatherParameters(0, 0, false, false);
            this.nextAt = now + cooldownTicks();
            setDirty();
        }
        if (this.nextAt == 0L) {
            this.nextAt = now + firstTicks();
            setDirty();
            return;
        }
        if (now < this.nextAt || recentRaid(level, now) || !anyoneExposed(level)) {
            return;
        }
        if (level.random.nextFloat() > (WetSeason.active ? 0.75f : 0.4f)) {
            this.nextAt = now + 20L * 60L * 4L;
            setDirty();
            return;
        }
        begin(level, now);
    }

    private void begin(ServerLevel level, long now) {
        int ticks = (int) Math.round(CanopyConfig.DOWNPOUR_MINUTES.get() * 60.0 * 20.0);
        this.until = now + ticks;
        this.torchesDoused = false;
        this.wasActive = true;
        this.nextAt = now + ticks + cooldownTicks();
        level.setWeatherParameters(0, ticks, true, true);
        for (ServerPlayer player : level.players()) {
            if (!exposed(player)) {
                continue;
            }
            player.displayClientMessage(Component.translatable("message.canopy.downpour"), true);
            JournalLearning.note(player, Journal.DOWNPOUR);
            CanopyAdvancements.award(player, "downpour");
        }
        affect(level);
        setDirty();
    }

    private void affect(ServerLevel level) {
        boolean douseTorches = !this.torchesDoused;
        for (ServerPlayer player : level.players()) {
            if (!exposed(player)) {
                continue;
            }
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 50, 0, false, false, true));
            BlockState below = level.getBlockState(player.blockPosition().below());
            if (player.onGround() && (below.is(BlockTags.DIRT) || below.is(Blocks.MUD) || below.is(Blocks.CLAY) || below.is(Blocks.SAND))) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, true));
            }
            player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> stats.setWetness(stats.wetness() + 0.05f));
            douseNear(level, player.blockPosition(), douseTorches);
        }
        if (douseTorches) {
            this.torchesDoused = true;
            setDirty();
        }
    }

    private static void douseNear(ServerLevel level, BlockPos center, boolean torches) {
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-8, -2, -8), center.offset(8, 3, 8))) {
            if (Shelter.solidAbove(level, pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.is(BlockTags.CAMPFIRES) && state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
                level.setBlock(pos, state.setValue(BlockStateProperties.LIT, false), Block.UPDATE_ALL);
                continue;
            }
            if (state.is(BlockTags.FIRE)) {
                level.removeBlock(pos, false);
                continue;
            }
            if (!torches) {
                continue;
            }
            if (state.is(Blocks.TORCH) || state.is(Blocks.WALL_TORCH)) {
                Block.popResource(level, pos, new ItemStack(Items.TORCH));
                level.removeBlock(pos, false);
            } else if (state.is(Blocks.SOUL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH)) {
                Block.popResource(level, pos, new ItemStack(Items.SOUL_TORCH));
                level.removeBlock(pos, false);
            }
        }
    }

    private boolean running(long now) {
        return this.until > now;
    }

    private static boolean anyoneExposed(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            if (exposed(player)) {
                return true;
            }
        }
        return false;
    }

    private static boolean exposed(Player player) {
        return !player.isCreative() && !player.isSpectator() && player.isAlive() && !Shelter.covered(player);
    }

    private static boolean recentRaid(ServerLevel level, long now) {
        for (ServerPlayer player : level.players()) {
            long wave = player.getPersistentData().getLong(MonkeyRaid.WAVE);
            if (wave != 0L && now - wave < 20L * 120L) {
                return true;
            }
        }
        return false;
    }

    private static long cooldownTicks() {
        long base = Math.round(CanopyConfig.EVENT_COOLDOWN_MINUTES.get() * 60.0 * 20.0);
        return WetSeason.active ? base / 2L : base;
    }

    private static long firstTicks() {
        return Math.max(20L * 120L, cooldownTicks() / 8L);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong("Until", this.until);
        tag.putLong("Next", this.nextAt);
        tag.putBoolean("Doused", this.torchesDoused);
        return tag;
    }
}
