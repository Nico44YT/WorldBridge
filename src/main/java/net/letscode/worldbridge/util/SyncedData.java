package net.letscode.worldbridge.util;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class SyncedData {
    protected NbtCompound nbt;

    public SyncedData(NbtCompound nbt) {
        this.nbt = nbt;
    }

    public NbtCompound getNbt() {
        return this.nbt;
    }

    public void setNbt(NbtCompound nbt) {
        this.nbt = nbt;
    }

    // * Fields * //
    public UUID getLevelUUID() {
        return this.nbt.getUuid("levelUUID");
    }

    public SyncedData setLevelUUID(UUID levelUUID) {
        nbt.putUuid("levelUUID", levelUUID);
        return this;
    }
}
