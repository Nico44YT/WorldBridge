package net.letscode.worldbridge.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.networking.packets.*;
import net.minecraft.util.Identifier;

public class WorldBridgePackets {

    public static final Identifier SYNC_PERSISTENT_DATA = WorldBridge.id("sync_persistent_data");
    public static final Identifier SEND_STORED_ENTITIES = WorldBridge.id("send_stored_entities");
    public static final Identifier REQUEST_STORED_ENTITIES = WorldBridge.id("request_stored_entities");

    public static final Identifier SAVE_ENTITY = WorldBridge.id("save_entity");
    public static final Identifier LOAD_ENTITY = WorldBridge.id("load_entity");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_STORED_ENTITIES, RequestStoredEntitiesC2S::receiveServer);

        ServerPlayNetworking.registerGlobalReceiver(SAVE_ENTITY, SaveEntityC2S::receiveServer);
        ServerPlayNetworking.registerGlobalReceiver(LOAD_ENTITY, LoadEntityC2S::receiveServer);
    }

    @Environment(EnvType.SERVER)
    public static void registerS2CPacketsServer() {

    }

    @Environment(EnvType.CLIENT)
    public static void registerS2CPacketsClient() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_PERSISTENT_DATA, SyncPersistentDataS2C::receiveClient);
        ClientPlayNetworking.registerGlobalReceiver(SEND_STORED_ENTITIES, SendStoredEntitiesS2C::receiveClient);
    }
}