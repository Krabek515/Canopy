package io.github.krabek515.canopy.client;

import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.network.SyncJournalPacket;
import net.minecraft.client.Minecraft;

public final class ClientJournal {
    private ClientJournal() {
    }

    public static void apply(SyncJournalPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        minecraft.player.getCapability(CanopyCapabilities.JOURNAL).ifPresent(journal -> journal.replace(packet.facts()));
        if (packet.open()) {
            minecraft.setScreen(new JournalScreen());
        }
    }
}
