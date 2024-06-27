package net.letscode.worldbridge.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.letscode.worldbridge.commands.WorldBridgeCommand;
import net.letscode.worldbridge.networking.WorldBridgePackets;
import net.letscode.worldbridge.screen.ModScreenRegistry;
import net.letscode.worldbridge.screen.custom.SoulExplorerScreen;
import net.letscode.worldbridge.util.EntityDataHolder;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

import java.util.ArrayList;
import java.util.List;

public class WorldBridgeClient implements ClientModInitializer {

    public static List<EntityDataHolder> entityDataHolders = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(WorldBridgeCommand::registerClient);

        WorldBridgePackets.registerS2CPacketsClient();

        HandledScreens.register(ModScreenRegistry.SOUL_EXPLORER_SCREEN_HANDLER, SoulExplorerScreen::new);
    }
}
