package net.letscode.worldbridge.loottable;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.letscode.worldbridge.item.ModItemRegistry;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

public class ModLoottableRegistry {
    private static final Identifier END_CITY_LOOT_TABLE_ID = LootTables.END_CITY_TREASURE_CHEST;
    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && END_CITY_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ModItemRegistry.SOUL_CRYSTAL).weight(1))
                        .with(ItemEntry.builder(Items.AIR).weight(9));


                tableBuilder.pool(poolBuilder);
            }
        });
    }
}
