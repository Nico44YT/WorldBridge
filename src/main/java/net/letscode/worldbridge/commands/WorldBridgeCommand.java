package net.letscode.worldbridge.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.letscode.worldbridge.WorldBridge;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.UUID;

public class WorldBridgeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                CommandManager.literal("wbserver")
                        .then(CommandManager.literal("worlduuid")
                                .executes(WorldBridgeCommand::getWorldUUID))
        );
    }

    public static void registerClient(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(
                ClientCommandManager.literal("wbclient")
                        .then(ClientCommandManager.literal("worlduuid")
                                .executes(WorldBridgeCommand::getClientWorldUUID))
        );
    }

    private static int getWorldUUID(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getServer();
        server.execute(() -> {
            UUID worldUUID = WorldBridge.syncedData.getLevelUUID();

            context.getSource().getPlayer().sendMessage(Text.literal("<Server> The UUID of this world is: " + worldUUID));
        });

        return 1;
    }

    private static int getClientWorldUUID(CommandContext<FabricClientCommandSource> context) {
        UUID worldUUID = WorldBridge.syncedData.getLevelUUID();

        context.getSource().getPlayer().sendMessage(Text.literal("<Client> The UUID of this world is: " + worldUUID));

        return 1;
    }
}
