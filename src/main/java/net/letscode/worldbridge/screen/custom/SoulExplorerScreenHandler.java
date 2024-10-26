package net.letscode.worldbridge.screen.custom;

import com.mojang.datafixers.util.Pair;
import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.block.custom.SoulExplorerBlockEntity;
import net.letscode.worldbridge.item.ModItemRegistry;
import net.letscode.worldbridge.screen.ModScreenRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class SoulExplorerScreenHandler extends ScreenHandler {
    static final Identifier EMPTY_SOUL_CRYSTAL_SLOT_TEXTURE = WorldBridge.id("item/empty_slot_soul_crystal");

    private final Inventory inventory;
    public final SoulExplorerBlockEntity blockEntity;

    public SoulExplorerScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public SoulExplorerScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenRegistry.SOUL_EXPLORER_SCREEN_HANDLER, syncId);
        checkSize((Inventory)blockEntity, 1);
        this.inventory = (Inventory)blockEntity;
        inventory.onOpen(playerInventory.player);
        this.blockEntity = (SoulExplorerBlockEntity)blockEntity;

        this.addSlot(new Slot(inventory, 0, 198-40, 14) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItemRegistry.SOUL_CRYSTAL);
            }

            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, SoulExplorerScreenHandler.EMPTY_SOUL_CRYSTAL_SLOT_TEXTURE);
            }
        });
        this.addSlot(new Slot(inventory, 1, 233-40, 14));
        this.addPlayerInventory(playerInventory);
        this.addPlayerHotbar(playerInventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if(slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 88 - 40 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 88 - 40 + i * 18, 142));
        }
    }
}
