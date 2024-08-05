package net.letscode.worldbridge;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.letscode.worldbridge.block.ModBlockRegistry;
import net.letscode.worldbridge.commands.WorldBridgeCommand;
import net.letscode.worldbridge.item.ModItemRegistry;
import net.letscode.worldbridge.loottable.ModLoottableRegistry;
import net.letscode.worldbridge.networking.WorldBridgePackets;
import net.letscode.worldbridge.networking.packets.SyncPersistentDataS2C;
import net.letscode.worldbridge.screen.ModScreenRegistry;
import net.letscode.worldbridge.sound.ModSoundRegistry;
import net.letscode.worldbridge.util.FileHandler;
import net.letscode.worldbridge.util.SyncedData;
import net.letscode.worldbridge.world.PersistentData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class WorldBridge implements ModInitializer {
    public static SyncedData syncedData;
    public static final String MOD_ID = "worldbridge";

    @Override
    public void onInitialize() {
        FileHandler.createBaseDirectory();
        AutoConfig.register(WorldBridgeConfig.class, GsonConfigSerializer::new);

        CommandRegistrationCallback.EVENT.register(WorldBridgeCommand::register);

        WorldBridgePackets.registerC2SPackets();

        ModItemRegistry.register();
        ModBlockRegistry.register();
        ModScreenRegistry.register();
        ModLoottableRegistry.register();
        ModSoundRegistry.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            syncedData = new SyncedData(PersistentData.getServerState(server).writeNbt(new NbtCompound()));
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            FileHandler.clearFiles();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            SyncPersistentDataS2C packet = new SyncPersistentDataS2C(syncedData);
            ServerPlayNetworking.send(handler.getPlayer(), packet);
        });
    }

    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }
}
