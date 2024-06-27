package net.letscode.worldbridge.networking.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.networking.PacketReceiver;
import net.letscode.worldbridge.networking.WorldBridgePackets;
import net.letscode.worldbridge.util.SyncedData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public record SyncPersistentDataS2C(SyncedData data) implements FabricPacket, PacketReceiver {
    public static final PacketType<SyncPersistentDataS2C> TYPE = PacketType.create(WorldBridgePackets.SYNC_PERSISTENT_DATA, SyncPersistentDataS2C::new);

    public SyncPersistentDataS2C(PacketByteBuf buf) {
        this(new SyncedData(buf.readNbt()));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeNbt(data.getNbt());
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void receiveClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        client.execute(() -> WorldBridge.syncedData = new SyncedData(buf.readNbt()));
    }
}
