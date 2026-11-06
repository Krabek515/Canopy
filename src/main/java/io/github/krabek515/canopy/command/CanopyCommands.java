package io.github.krabek515.canopy.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.body.WoundCare;
import io.github.krabek515.canopy.entity.MonkeyRaid;
import io.github.krabek515.canopy.world.Bait;
import io.github.krabek515.canopy.world.ExpeditionCamp;
import io.github.krabek515.canopy.world.GasPocket;
import io.github.krabek515.canopy.world.ExposedVein;
import io.github.krabek515.canopy.world.GiantTree;
import io.github.krabek515.canopy.world.Downpour;
import io.github.krabek515.canopy.world.FlashFlood;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.network.CanopyNetwork;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(modid = Canopy.MOD_ID)
public final class CanopyCommands {
    private CanopyCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("canopy")
                .executes(context -> {
                    context.getSource().sendSuccess(() -> Component.translatable("command.canopy.help"), false);
                    return 1;
                })
                .then(locate())
                .then(stat("temperature", 0f, SurvivalStats.TEMP_MAX, SurvivalStats::setTemperature))
                .then(stat("wetness", 0f, 1f, SurvivalStats::setWetness))
                .then(wound())
                .then(Commands.literal("census").requires(source -> source.hasPermission(2)).executes(context -> {
                    net.minecraft.server.level.ServerLevel level = context.getSource().getLevel();
                    net.minecraft.core.BlockPos center = context.getSource().getEntity() != null
                            ? context.getSource().getEntity().blockPosition()
                            : level.getSharedSpawnPos();
                    String report = io.github.krabek515.canopy.world.Census.report(level, center, 160);
                    context.getSource().sendSuccess(() -> Component.literal(report), false);
                    return 1;
                }))
                .then(Commands.literal("downpour").requires(source -> source.hasPermission(2)).executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    Downpour.get(player.serverLevel()).force(player.serverLevel());
                    context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", "downpour", "now"), true);
                    return 1;
                }))
                .then(Commands.literal("raid").requires(source -> source.hasPermission(2)).executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    MonkeyRaid.begin(player.serverLevel(), player);
                    context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", "raid", "now"), true);
                    return 1;
                }))
                .then(Commands.literal("flood").requires(source -> source.hasPermission(2)).executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    FlashFlood.force(player);
                    context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", "flood", "now"), true);
                    return 1;
                }))
                .then(Commands.literal("bait").requires(source -> source.hasPermission(2)).executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    if (!Bait.place(player)) {
                        context.getSource().sendFailure(Component.translatable("command.canopy.bait_blocked"));
                        return 0;
                    }
                    context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", "bait", "now"), true);
                    return 1;
                }))
                .then(Commands.literal("tree").requires(source -> source.hasPermission(2)).executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    net.minecraft.core.BlockPos origin = player.blockPosition().relative(player.getDirection(), 4);
                    if (!GiantTree.grow(player.serverLevel(), origin, player.serverLevel().random)) {
                        context.getSource().sendFailure(Component.translatable("command.canopy.tree_blocked"));
                        return 0;
                    }
                    context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", "tree", "now"), true);
                    return 1;
                }))
                .then(Commands.literal("vein").requires(source -> source.hasPermission(2)).executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    net.minecraft.core.BlockPos pos = player.blockPosition().relative(player.getDirection(), 2);
                    if (player.serverLevel().getBlockState(pos).isAir()) {
                        pos = pos.below();
                    }
                    if (!ExposedVein.force(player.serverLevel(), pos)) {
                        context.getSource().sendFailure(Component.translatable("command.canopy.vein_blocked"));
                        return 0;
                    }
                    context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", "vein", "now"), true);
                    return 1;
                }))
                .then(Commands.literal("camp").requires(source -> source.hasPermission(2)).executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    net.minecraft.core.BlockPos origin = player.blockPosition().relative(player.getDirection(), 3);
                    if (!ExpeditionCamp.grow(player.serverLevel(), origin)) {
                        context.getSource().sendFailure(Component.translatable("command.canopy.camp_blocked"));
                        return 0;
                    }
                    context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", "camp", "now"), true);
                    return 1;
                }))
                .then(Commands.literal("gas").requires(source -> source.hasPermission(2)).executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    if (!GasPocket.force(player.serverLevel(), player.blockPosition())) {
                        context.getSource().sendFailure(Component.translatable("command.canopy.gas_blocked"));
                        return 0;
                    }
                    context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", "gas", "now"), true);
                    return 1;
                })));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> locate() {
        return Commands.literal("locate").executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            com.mojang.datafixers.util.Pair<net.minecraft.core.BlockPos, net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome>> found =
                    player.serverLevel().findClosestBiome3d(
                            holder -> holder.unwrapKey().map(key -> Canopy.MOD_ID.equals(key.location().getNamespace())).orElse(false),
                            player.blockPosition(), 6400, 32, 64);
            if (found == null) {
                context.getSource().sendFailure(Component.translatable("command.canopy.locate_none"));
                return 0;
            }
            net.minecraft.core.BlockPos pos = found.getFirst();
            String id = found.getSecond().unwrapKey().map(key -> key.location().toString()).orElse("?");
            context.getSource().sendSuccess(() -> Component.translatable("command.canopy.locate",
                    id, pos.getX(), pos.getY(), pos.getZ()), false);
            return 1;
        });
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> wound() {
        return Commands.literal("wound").requires(source -> source.hasPermission(2))
                .then(woundStage("open", SurvivalStats.WOUND_OPEN, true))
                .then(woundStage("clean", SurvivalStats.WOUND_OPEN, false))
                .then(woundStage("infect", SurvivalStats.WOUND_INFECTED, true))
                .then(woundStage("fever", SurvivalStats.WOUND_FEVER, true))
                .then(woundStage("clear", SurvivalStats.WOUND_NONE, false));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> woundStage(String name, int stage, boolean dirty) {
        return Commands.literal(name).executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            long now = player.level().getGameTime();
            player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> {
                stats.assignWound(stage, dirty, now);
                if (stage == SurvivalStats.WOUND_NONE) {
                    stats.setBleedingUntil(0L);
                    WoundCare.clearFever(player);
                } else {
                    WoundCare.applyEffects(player, stats);
                }
            });
            CanopyNetwork.send(player);
            context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", "wound", name), true);
            return 1;
        });
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> stat(
            String name, float min, float max, BiConsumer<SurvivalStats, Float> setter) {
        return Commands.literal(name).requires(source -> source.hasPermission(2))
                .then(Commands.argument("value", FloatArgumentType.floatArg(min, max))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            float value = FloatArgumentType.getFloat(context, "value");
                            player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats -> setter.accept(stats, value));
                            CanopyNetwork.send(player);
                            context.getSource().sendSuccess(() -> Component.translatable("command.canopy.set", name, value), true);
                            return 1;
                        }));
    }
}
