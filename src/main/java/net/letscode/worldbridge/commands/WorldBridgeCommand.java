package net.letscode.worldbridge.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.WorldBridgeConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import java.util.ArrayList;
import java.util.UUID;

public class WorldBridgeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                CommandManager.literal("worldbridge")

                        .then(CommandManager.literal("worlduuid")
                                .executes(WorldBridgeCommand::getWorldUUID))

                        .then(CommandManager.literal("entitytype")
                                .then(CommandManager.argument("entity", RegistryEntryArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.ENTITY_TYPE))
                                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                        .executes(WorldBridgeCommand::getEntityType)))
        );
    }

    public static void registerClient(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {

    }

    private static int getWorldUUID(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getServer();
        server.execute(() -> {
            UUID worldUUID = WorldBridge.syncedData.getLevelUUID();

            context.getSource().getPlayer().sendMessage(Text.literal("<Server> The UUID of this world is: " + worldUUID));
        });

        return 1;
    }

    private static int getEntityType(CommandContext<ServerCommandSource> context) {
        try{
            Entity en = RegistryEntryArgumentType.getSummonableEntityType(context, "entity").value().create(context.getSource().getWorld());
            //.withColor(Colors.LIGHT_GRAY)
            //.withColor(Colors.LIGHT_GRAY)
            context.getSource().getPlayer().sendMessage(Text.translatable("command.worldbridge.entitytype", Text.empty().append(en.getName()), Text.literal(en.getType().toString())));
        } catch (Exception ignore) {
            ignore.printStackTrace();
            return 0;
        };
        return 1;
    }


    private static int getClientWorldUUID(CommandContext<FabricClientCommandSource> context) {
        UUID worldUUID = WorldBridge.syncedData.getLevelUUID();

        context.getSource().getPlayer().sendMessage(Text.literal("<Client> The UUID of this world is: " + worldUUID));

        return 1;
    }
}
