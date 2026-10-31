package io.github.krabek515.canopy.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import io.github.krabek515.canopy.Canopy;
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
                .requires(source -> source.hasPermission(2))
                .then(stat("thirst", 0f, SurvivalStats.NEED_MAX, SurvivalStats::setThirst))
                .then(stat("temperature", 0f, SurvivalStats.TEMP_MAX, SurvivalStats::setTemperature))
                .then(stat("wetness", 0f, 1f, SurvivalStats::setWetness))
                .then(stat("stamina", 0f, SurvivalStats.NEED_MAX, SurvivalStats::setStamina)));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<net.minecraft.commands.CommandSourceStack> stat(
            String name, float min, float max, BiConsumer<SurvivalStats, Float> setter) {
        return Commands.literal(name)
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
