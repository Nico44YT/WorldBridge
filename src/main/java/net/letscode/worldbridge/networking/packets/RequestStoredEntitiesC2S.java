package net.letscode.worldbridge.networking.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.WorldBridgeConfig;
import net.letscode.worldbridge.networking.PacketReceiver;
import net.letscode.worldbridge.networking.WorldBridgePackets;
import net.letscode.worldbridge.util.EntityDataHolder;
import net.letscode.worldbridge.util.FileHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public record RequestStoredEntitiesC2S() implements FabricPacket, PacketReceiver {
    public static final PacketType<RequestStoredEntitiesC2S> TYPE = PacketType.create(WorldBridgePackets.REQUEST_STORED_ENTITIES, RequestStoredEntitiesC2S::new);

    public RequestStoredEntitiesC2S(PacketByteBuf buf) {
        this();
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void receiveServer(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            List<EntityDataHolder> holders = FileHandler.getFiles();
            List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList();

            for (ServerPlayerEntity serverPlayer : playerList) {
                List<EntityDataHolder> filteredHolders = holders.stream()
                        .filter(holder -> isValidHolder(holder, serverPlayer))
                        .toList();

                ServerPlayNetworking.send(serverPlayer, new SendStoredEntitiesS2C(filteredHolders));
            }
        });
    }

    private static boolean isValidHolder(EntityDataHolder holder, ServerPlayerEntity serverPlayer) {
        if(WorldBridgeConfig.getConfigHolder().blacklisted_entities.contains(holder.getEntityType().toString())) {
            return false;
        }
        if(holder.delete) {
            return false;
        }
        if(!holder.playerUUID.equals(serverPlayer.getUuid()) && WorldBridgeConfig.getConfigHolder().only_uploader_access) {
            return false;
        }
        if(WorldBridgeConfig.getConfigHolder().allow_transfer_between_worlds) {
            return true;
        }

        return holder.levelUUID.equals(WorldBridge.syncedData.getLevelUUID());
    }
}
