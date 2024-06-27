package net.letscode.worldbridge.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityDataHolder {
    public UUID levelUUID;
    public UUID entityUUID;
    public UUID playerUUID;
    public String nbtData;
    public String entityType;
    public boolean delete;

    public EntityDataHolder(UUID playerUUID, UUID entityUUID, String entityType, String nbtData, UUID levelUUID) {
        this.playerUUID = playerUUID;
        this.entityUUID = entityUUID;
        this.entityType = entityType;
        this.nbtData = nbtData;
        this.delete = false;
        this.levelUUID = levelUUID;
    }

    public EntityDataHolder(UUID playerUUID, UUID entityUUID, EntityType<?> entityType, NbtCompound nbtData, UUID levelUUID) {
        this.playerUUID = playerUUID;
        this.entityUUID = entityUUID;
        this.entityType = entityType.toString();
        this.nbtData = nbtData.asString();
        this.delete = false;
        this.levelUUID = levelUUID;
    }

    public EntityDataHolder(EntityDataHolder dataHolder) {
        this.playerUUID = dataHolder.playerUUID;
        this.entityUUID = dataHolder.entityUUID;
        this.entityType = dataHolder.entityType;
        this.nbtData = dataHolder.nbtData;
        this.levelUUID = dataHolder.levelUUID;
        this.delete = dataHolder.delete;
    }

    public EntityDataHolder(NbtCompound nbt) {
        EntityDataHolder dataHolder = fromNbt(nbt);

        this.playerUUID = dataHolder.playerUUID;
        this.entityUUID = dataHolder.entityUUID;
        this.entityType = dataHolder.entityType;
        this.nbtData = dataHolder.nbtData;
        this.levelUUID = dataHolder.levelUUID;
        this.delete = dataHolder.delete;
    }

    public EntityDataHolder setDelete(boolean delete) {
        this.delete = delete;
        return this;
    }

    public boolean shouldDelete() {
        return this.delete;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putUuid("playerUUID", playerUUID);
        nbt.putUuid("entityUUID", entityUUID);
        nbt.putString("entityType",entityType);
        nbt.putString("nbtData", nbtData);
        nbt.putUuid("levelUUID", levelUUID);

        return nbt;
    }

    public static EntityDataHolder fromNbt(@NotNull NbtCompound nbt) {
        return new EntityDataHolder(
                nbt.getUuid("playerUUID"),
                nbt.getUuid("entityUUID"),
                nbt.getString("entityType"),
                nbt.getString("nbtData"),
                nbt.getUuid("levelUUID")
        );
    }

    public static EntityType<?> getEntityTypeFromString(@NotNull String string) {
        return Registries.ENTITY_TYPE.stream()
                .filter(entry -> entry.toString().equals(string))
                .findFirst()
                .orElse(null);
    }

    public EntityType<?> getEntityType() {
        return getEntityTypeFromString(this.entityType);
    }

    public NbtCompound getEntityNBT() {
        try{
            return NbtHelper.fromNbtProviderString(nbtData);
        } catch (Exception ignore) {
            return new NbtCompound();
        }
    }

    public Entity createEntity(@NotNull World world) {
        EntityType<?> realEntityType = getEntityType();
        Entity entity = realEntityType.create(world);

        entity.setUuid(entityUUID);
        entity.readNbt(getEntityNBT());

        return entity;
    }

    public Text getName(@NotNull World world) {
        Entity newEntity = createEntity(world);

        if(newEntity.hasCustomName()) {
            return Text.empty().append(newEntity.getCustomName()).withColor(Colors.GRAY).styled(style -> style.withItalic(true));
        }

        if(newEntity.writeNbt(new NbtCompound()).getInt("Age") < 0) {
            return Text.empty().append(Text.translatable("container.worldbridge.baby")).append(" ").append(newEntity.getName()).withColor(Colors.GRAY);
        } else {
            return Text.empty().append(newEntity.getName()).withColor(Colors.GRAY);
        }
    }
}
