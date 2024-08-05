package net.letscode.worldbridge.client;

import net.fabricmc.api.ClientModInitializer;
import net.letscode.worldbridge.networking.WorldBridgePackets;
import net.letscode.worldbridge.screen.ModScreenRegistry;
import net.letscode.worldbridge.screen.custom.SoulExplorerScreen;
import net.letscode.worldbridge.util.EntityDataHolder;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

import java.util.ArrayList;
import java.util.List;

public class WorldBridgeClient implements ClientModInitializer {

    public static List<EntityDataHolder> entityDataHolders = new ArrayList<>();
    public static int save_load_xp_cost = 0;

    @Override
    public void onInitializeClient() {
        //ClientCommandRegistrationCallback.EVENT.register(WorldBridgeCommand::registerClient);

        WorldBridgePackets.registerS2CPacketsClient();

        HandledScreens.register(ModScreenRegistry.SOUL_EXPLORER_SCREEN_HANDLER, SoulExplorerScreen::new);
    }
}
