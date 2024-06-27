package net.letscode.worldbridge.networking.packets;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.letscode.worldbridge.block.custom.SoulExplorerBlock;
import net.letscode.worldbridge.block.custom.SoulExplorerBlockEntity;
import net.letscode.worldbridge.item.ModItemRegistry;
import net.letscode.worldbridge.networking.PacketReceiver;
import net.letscode.worldbridge.networking.WorldBridgePackets;
import net.letscode.worldbridge.util.EntityDataHolder;
import net.letscode.worldbridge.util.FileHandler;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record SaveEntityC2S(BlockPos blockPos) implements FabricPacket, PacketReceiver {
    public static final PacketType<SaveEntityC2S> TYPE = PacketType.create(WorldBridgePackets.SAVE_ENTITY, SaveEntityC2S::new);

    public SaveEntityC2S(PacketByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(blockPos);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static void receiveServer(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos blockPos = buf.readBlockPos();
        World world = player.getWorld();

        server.execute(() -> {
           if(!world.isChunkLoaded(blockPos)) return;

           BlockState state = world.getBlockState(blockPos);

            if(!(state.getBlock() instanceof SoulExplorerBlock)) return;

            SoulExplorerBlockEntity blockEntity = (SoulExplorerBlockEntity) world.getBlockEntity(blockPos);

            ItemStack stack = blockEntity.getStack(SoulExplorerBlockEntity.INPUT_SLOT);
            if(stack.isEmpty()) return;
            if(!stack.hasNbt() || !stack.getNbt().contains("entityType")) return;

            EntityDataHolder dataHolder = new EntityDataHolder(stack.getNbt());
            dataHolder.playerUUID = player.getUuid();

            FileHandler.writeFile(dataHolder);

            blockEntity.setStack(SoulExplorerBlockEntity.INPUT_SLOT, ItemStack.EMPTY);
            blockEntity.setStack(SoulExplorerBlockEntity.OUTPUT_SLOT, new ItemStack(ModItemRegistry.SOUL_CRYSTAL));
            blockEntity.markDirty();
        });
    }
}
