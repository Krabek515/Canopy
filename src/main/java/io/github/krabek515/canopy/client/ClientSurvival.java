package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.network.SyncSurvivalPacket;
import net.minecraft.client.Minecraft;

public final class ClientSurvival {
    private ClientSurvival() {
    }

    public static void apply(SyncSurvivalPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        minecraft.player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(packet::copyTo);
    }
}
