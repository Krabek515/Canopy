package io.github.krabek515.canopy.network;

import io.github.krabek515.canopy.Canopy;
import io.github.krabek515.canopy.capability.CanopyCapabilities;
import io.github.krabek515.canopy.journal.Journal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class CanopyNetwork {
    private static final String PROTOCOL = "5";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Canopy.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int nextId;

    private CanopyNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(nextId++, SyncSurvivalPacket.class, SyncSurvivalPacket::encode, SyncSurvivalPacket::decode, SyncSurvivalPacket::handle);
        CHANNEL.registerMessage(nextId++, SyncJournalPacket.class, SyncJournalPacket::encode, SyncJournalPacket::decode, SyncJournalPacket::handle);
    }

    public static void send(ServerPlayer player) {
        player.getCapability(CanopyCapabilities.SURVIVAL).ifPresent(stats ->
                CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), SyncSurvivalPacket.from(stats)));
    }

    public static void sendJournal(ServerPlayer player, Journal journal, boolean open) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), SyncJournalPacket.from(journal, open));
    }
}
