package net.letscode.worldbridge.networking.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.letscode.worldbridge.WorldBridgeConfig;
import net.letscode.worldbridge.client.WorldBridgeClient;
import net.letscode.worldbridge.networking.PacketReceiver;
import net.letscode.worldbridge.networking.WorldBridgePackets;
import net.letscode.worldbridge.util.EntityDataHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public record SendStoredEntitiesS2C(List<EntityDataHolder> dataHolderList) implements FabricPacket, PacketReceiver {
    public static final PacketType<SendStoredEntitiesS2C> TYPE = PacketType.create(WorldBridgePackets.SEND_STORED_ENTITIES, SendStoredEntitiesS2C::new);

    public SendStoredEntitiesS2C(PacketByteBuf buf) {
        this(readFromBuffer(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(dataHolderList.size());

        for(EntityDataHolder holder : dataHolderList) {
            buf.writeNbt(holder.toNbt());
        }

        buf.writeInt(WorldBridgeConfig.getConfigHolder().save_load_xp_cost);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static List<EntityDataHolder> readFromBuffer(PacketByteBuf buf) {
        List<EntityDataHolder> holders = new ArrayList<>();

        int length = buf.readInt();
        for(int i = 0;i<length;i++) {
            holders.add(new EntityDataHolder(buf.readNbt()));
        }

        return holders;
    }

    public static void receiveClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        List<EntityDataHolder> holders = readFromBuffer(buf);
        int xp_cost = buf.readInt();

        client.execute(() -> {
            WorldBridgeClient.entityDataHolders.clear();
            WorldBridgeClient.entityDataHolders = holders;
            WorldBridgeClient.save_load_xp_cost = xp_cost;
        });
    }
}
