package io.github.krabek515.canopy.network;

import io.github.krabek515.canopy.client.ClientJournal;
import io.github.krabek515.canopy.journal.Journal;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record SyncJournalPacket(List<String> facts, boolean open) {
    public static SyncJournalPacket from(Journal journal, boolean open) {
        return new SyncJournalPacket(List.copyOf(journal.facts()), open);
    }

    public static void encode(SyncJournalPacket packet, FriendlyByteBuf buf) {
        buf.writeCollection(packet.facts, FriendlyByteBuf::writeUtf);
        buf.writeBoolean(packet.open);
    }

    public static SyncJournalPacket decode(FriendlyByteBuf buf) {
        return new SyncJournalPacket(buf.readList(FriendlyByteBuf::readUtf), buf.readBoolean());
    }

    public static void handle(SyncJournalPacket packet, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.setPacketHandled(true);
            return;
        }
        ctx.enqueueWork(() -> ClientJournal.apply(packet));
        ctx.setPacketHandled(true);
    }
}
