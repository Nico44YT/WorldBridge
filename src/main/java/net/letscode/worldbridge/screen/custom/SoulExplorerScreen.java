package net.letscode.worldbridge.screen.custom;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.block.custom.SoulExplorerBlockEntity;
import net.letscode.worldbridge.client.WorldBridgeClient;
import net.letscode.worldbridge.item.custom.SoulCrystal;
import net.letscode.worldbridge.networking.packets.LoadEntityC2S;
import net.letscode.worldbridge.networking.packets.RequestStoredEntitiesC2S;
import net.letscode.worldbridge.networking.packets.SaveEntityC2S;
import net.letscode.worldbridge.util.EntityDataHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SoulExplorerScreen extends HandledScreen<SoulExplorerScreenHandler> {
    private final PlayerInventory playerInventory;

    private static final Identifier TEXTURE = WorldBridge.id("textures/gui/container/soul_explorer.png");
    private static final Identifier ENTITY_ENTRY_TEXTURE = WorldBridge.id("textures/gui/container/entity_field.png");

    private ButtonWidget leftButton;
    private ButtonWidget rightButton;
    private ButtonWidget saveButton;
    private ButtonWidget loadButton;

    private ButtonWidget[] buttons;

    private int selectedEntity;
    private int page;

    public SoulExplorerScreen(SoulExplorerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;
    }


    @Override
    protected void init() {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        super.init();

        this.backgroundWidth = 256;
        this.playerInventoryTitleX = 48;
        this.playerInventoryTitleY = 74;
        this.titleX = 48;
        this.titleY = 4;

        this.leftButton = ButtonWidget.builder(Text.literal("<-"), button -> changePage(-1))
                .size(53 / 2, 72 - 60)
                .position(197 + x - 40, 61 + y)
                .build();

        this.rightButton = ButtonWidget.builder(Text.literal("->"), button -> changePage(1))
                .size(53 / 2, 72 - 60)
                .position(197 + 53 / 2 + x - 40 + 1, 61 + y)
                .build();

        this.loadButton = ButtonWidget.builder(Text.translatable("container.worldbridge.soul_explorer.load"), this::loadEntity).size(53, 12).position(197 + x - 40, 48 + y).build();

        this.saveButton = ButtonWidget.builder(Text.translatable("container.worldbridge.soul_explorer.save"), this::saveEntity).size(53, 12).position(197 + x - 40, 35 + y).build();

        this.buttons = new ButtonWidget[]{leftButton, rightButton, saveButton, loadButton};
    }

    private void changePage(int amount) {
        page = Math.max(0, page + amount);
    }

    public void loadEntity(ButtonWidget button) {
        ClientPlayNetworking.send(new LoadEntityC2S(handler.blockEntity.getPos(), WorldBridgeClient.entityDataHolders.get(selectedEntity)));
        ClientPlayNetworking.send(new RequestStoredEntitiesC2S());
    }

    public void saveEntity(ButtonWidget button) {
        ClientPlayNetworking.send(new SaveEntityC2S(handler.blockEntity.getPos()));
        ClientPlayNetworking.send(new RequestStoredEntitiesC2S());
    }

    private boolean isSaveButtonActive() {
        return this.handler.getSlot(SoulExplorerBlockEntity.OUTPUT_SLOT).getStack().isEmpty() &&
                this.handler.getSlot(SoulExplorerBlockEntity.INPUT_SLOT).getStack().hasNbt() &&
                this.handler.getSlot(SoulExplorerBlockEntity.INPUT_SLOT).getStack().getItem() instanceof SoulCrystal;
    }

    private boolean isLoadButtonActive() {
        return this.handler.getSlot(SoulExplorerBlockEntity.OUTPUT_SLOT).getStack().isEmpty() &&
                !this.handler.getSlot(SoulExplorerBlockEntity.INPUT_SLOT).getStack().hasNbt() &&
                this.handler.getSlot(SoulExplorerBlockEntity.INPUT_SLOT).getStack().getItem() instanceof SoulCrystal &&
                !WorldBridgeClient.entityDataHolders.isEmpty();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        leftButton.active = page != 0;
        rightButton.active = WorldBridgeClient.entityDataHolders.size() / 6 > page;
        saveButton.active = isSaveButtonActive();
        loadButton.active = isLoadButtonActive();

        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        renderEntityEntries(context);
        drawMouseoverTooltip(context, mouseX, mouseY);

        for (ButtonWidget button : buttons) {
            button.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        try {
            if (!WorldBridgeClient.entityDataHolders.isEmpty() && selectedEntity < WorldBridgeClient.entityDataHolders.size()) {
                EntityDataHolder dataHolder = WorldBridgeClient.entityDataHolders.get(selectedEntity);
                LivingEntity entity = (LivingEntity) dataHolder.createEntity(playerInventory.player.getWorld());
                entity.readNbt(dataHolder.getEntityNBT());
                InventoryScreen.drawEntity(context, x + 6, y + 6, x + 76, y + 77, 20, 0.25F, mouseX, mouseY, entity);
            }
        } catch (Exception ignore) {}
    }

    public void renderEntityEntries(DrawContext context) {
        World world = playerInventory.player.getWorld();
        for(int entityIndex = page * 5; entityIndex < WorldBridgeClient.entityDataHolders.size() && entityIndex < (page + 1)*5; entityIndex++) {
            renderEntityEntry(context, entityIndex, world);
        }
    }

    public void renderEntityEntry(DrawContext context, int index, World world) {
        EntityDataHolder entityData = WorldBridgeClient.entityDataHolders.get(index);

        Entity entity = entityData.getEntityType().create(world);

        entity.readNbt(entityData.getEntityNBT());

        int offset = index - page * 5;
        int xPos = (width - backgroundWidth) / 2 + 89;
        int yPos = (height - backgroundHeight) / 2 + 17 + (12 * offset);

        context.drawTexture(ENTITY_ENTRY_TEXTURE, xPos - 2, yPos - 4, 0, selectedEntity == index ? 12 : 0, 107, 12);

        Text name = entityData.getName(world);
        if(this.textRenderer.getWidth(name.getString()) > 102) {

        }

        context.drawTextWithShadow(this.textRenderer, name.asOrderedText(), xPos + 1, yPos - 2, Colors.WHITE);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        int xPos = (width - backgroundWidth) / 2;
        int yPos = (height - backgroundHeight) / 2;

        for (int i = 0; i < 5; i++) {
            if (mouseX >= 87 + xPos && mouseX <= 193 + xPos &&
                    mouseY >= 13 + yPos + 12 * i && mouseY <= 24 + yPos + 12 * i) {
                selectedEntity = page * 5 + i;
                return true;
            }
        }

        for (ButtonWidget button : buttons) {
            if (mouseX >= button.getX() && mouseX <= button.getX() + button.getWidth() &&
                    mouseY >= button.getY() && mouseY <= button.getY() + button.getHeight()) {
                button.mouseClicked(mouseX, mouseY, mouseButton);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        reset();
    }

    public void reset() {
        SoulExplorerScreen screen =new SoulExplorerScreen(this.handler, this.playerInventory, this.title);
        screen.page = page;
        screen.selectedEntity = selectedEntity;

        client.setScreen(screen);
    }
}
