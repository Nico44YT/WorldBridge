package net.letscode.worldbridge.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.block.custom.SoulExplorerBlock;
import net.letscode.worldbridge.block.custom.SoulExplorerBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlockRegistry {
    public static Block SOUL_EXPLORER = registerBlock("soul_explorer", new SoulExplorerBlock(
            AbstractBlock.Settings.create()
                    .strength(2.0f, 2.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .luminance(state -> 4)
                    .requiresTool()
                    .allowsSpawning(Blocks::never)
    ));

    public static final BlockEntityType<SoulExplorerBlockEntity> SOUL_EXPLORER_BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            WorldBridge.id("soul_explorer_block_entity"),
            FabricBlockEntityTypeBuilder.create(SoulExplorerBlockEntity::new, SOUL_EXPLORER).build()
    );


    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.addAfter(Blocks.BEACON, SOUL_EXPLORER);
        });

    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, WorldBridge.id(name), block);
    }

    private static Item registerBlockItem(String name, Block block){
        return Registry.register(
                Registries.ITEM,
                WorldBridge.id(name),
                new BlockItem(block, new FabricItemSettings()));
    }
}
