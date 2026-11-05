package io.github.krabek515.canopy.entity;

import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.item.CanopyItems;
import io.github.krabek515.canopy.item.WornClothing;
import net.minecraft.world.entity.player.Player;

public final class Scent {
    private Scent() {
    }

    public static boolean smells(Player player) {
        if (player.isCreative() || player.isSpectator()) {
            return false;
        }
        boolean bleeding = player.getCapability(CanopyCapabilities.SURVIVAL)
                .map(stats -> stats.wound() != SurvivalStats.WOUND_NONE || stats.isBleeding(player.level().getGameTime()))
                .orElse(false);
        if (bleeding) {
            return true;
        }
        for (var stack : player.getInventory().items) {
            if (stack.is(CanopyItems.RAW_MEAT.get())) {
                return true;
            }
        }
        return player.getOffhandItem().is(CanopyItems.RAW_MEAT.get());
    }

    public static double jaguarRange(Player player) {
        double range = smells(player) ? 36.0 : player.level().isThundering() ? 28.0 : player.level().isRaining() ? 16.0 : 12.0;
        range *= 1.0 + WornClothing.noise(player);
        return Math.min(48.0, range);
    }
}
