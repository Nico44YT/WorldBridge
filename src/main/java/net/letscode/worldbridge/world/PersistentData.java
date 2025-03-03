package net.letscode.worldbridge.world;

import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.util.nbt.NbtCompoundBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.UUID;

public class PersistentData extends PersistentState {
    private static Type<PersistentData> type = new Type<>(PersistentData::new, PersistentData::createFromNbt, null);

    public UUID levelUUID = UUID.randomUUID();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return NbtCompoundBuilder.create()
                .putUUID("levelUUID", levelUUID)
                .build();
    }

    public static PersistentData createFromNbt(NbtCompound nbt) {
        PersistentData state = new PersistentData();
        state.levelUUID = nbt.getUuid("levelUUID");
        return state;
    }

    public static PersistentData getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getOverworld().getPersistentStateManager();

        PersistentData state = persistentStateManager.getOrCreate(type, WorldBridge.MOD_ID);

        state.markDirty();

        return state;
    }
}
