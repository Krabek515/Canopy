package io.github.krabek515.canopy.progress;

import io.github.krabek515.canopy.Canopy;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class CanopyAdvancements {
    private CanopyAdvancements() {
    }

    public static void award(ServerPlayer player, String path) {
        if (player.getServer() == null) {
            return;
        }
        Advancement advancement = player.getServer().getAdvancements().getAdvancement(ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, path));
        if (advancement == null) {
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) {
            return;
        }
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(advancement, criterion);
        }
    }
}
