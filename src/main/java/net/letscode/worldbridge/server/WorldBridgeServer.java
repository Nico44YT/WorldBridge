package net.letscode.worldbridge.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.letscode.worldbridge.networking.WorldBridgePackets;

public class WorldBridgeServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        WorldBridgePackets.registerS2CPacketsServer();
    }
}
