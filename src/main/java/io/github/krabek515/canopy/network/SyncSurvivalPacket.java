package io.github.krabek515.canopy.network;

import io.github.krabek515.canopy.capability.SurvivalStats;
import io.github.krabek515.canopy.client.ClientSurvival;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncSurvivalPacket(float thirst, float temperature, float wetness, float stamina, float hand, long recoveringUntil, long bleedingUntil, int wound, boolean woundDirty, long floodWarnUntil, long floodWaveUntil) {
    public static SyncSurvivalPacket from(SurvivalStats stats) {
        return new SyncSurvivalPacket(stats.thirst(), stats.temperature(), stats.wetness(), stats.stamina(), stats.hand(), stats.recoveringUntil(), stats.bleedingUntil(), stats.wound(), stats.woundDirty(), stats.floodWarnUntil(), stats.floodWaveUntil());
    }

    public static void encode(SyncSurvivalPacket packet, FriendlyByteBuf buf) {
        buf.writeFloat(packet.thirst);
        buf.writeFloat(packet.temperature);
        buf.writeFloat(packet.wetness);
        buf.writeFloat(packet.stamina);
        buf.writeFloat(packet.hand);
        buf.writeLong(packet.recoveringUntil);
        buf.writeLong(packet.bleedingUntil);
        buf.writeVarInt(packet.wound);
        buf.writeBoolean(packet.woundDirty);
        buf.writeLong(packet.floodWarnUntil);
        buf.writeLong(packet.floodWaveUntil);
    }

    public static SyncSurvivalPacket decode(FriendlyByteBuf buf) {
        return new SyncSurvivalPacket(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readLong(), buf.readLong(), buf.readVarInt(), buf.readBoolean(), buf.readLong(), buf.readLong());
    }

    public static void handle(SyncSurvivalPacket packet, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        if (ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.setPacketHandled(true);
            return;
        }
        ctx.enqueueWork(() -> ClientSurvival.apply(packet));
        ctx.setPacketHandled(true);
    }

    public void copyTo(SurvivalStats stats) {
        stats.setThirst(thirst);
        stats.setTemperature(temperature);
        stats.setWetness(wetness);
        stats.setStamina(stamina);
        stats.setHand(hand);
        stats.setRecoveringUntil(recoveringUntil);
        stats.setBleedingUntil(bleedingUntil);
        stats.setWound(wound);
        stats.setWoundDirty(woundDirty);
        stats.setFloodWarnUntil(floodWarnUntil);
        stats.setFloodWaveUntil(floodWaveUntil);
    }
}
