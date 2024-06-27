package net.letscode.worldbridge.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.item.custom.SoulCrystal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItemRegistry {

    public static final Item SOUL_CRYSTAL = registerItem("soul_crystal", new SoulCrystal(new FabricItemSettings().maxCount(1)));

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addAfter(Items.ENDER_EYE, SOUL_CRYSTAL);
        });
    }

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, WorldBridge.id(name), item);
    }
}
