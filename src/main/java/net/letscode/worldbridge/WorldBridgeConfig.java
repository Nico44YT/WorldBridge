package net.letscode.worldbridge;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;

@Config(name = "worldbridge")
public class WorldBridgeConfig implements ConfigData {

    public boolean enable_soul_explorer = true;
    public boolean enable_soul_tablet = true;

    @Comment(value = "Will determine how many times the soul tablet can be opened, -1 will make it unbreakable. Will require game restart to apply")
    public int soul_tablet_durability = 64;

    @Comment(value = "The XP cost to save or load a soul in the soul explorer")
    public int save_load_xp_cost = 3;

    public boolean allow_transfer_between_worlds = false;
    public boolean allow_inventory_transfer = false;
    public boolean only_uploader_access = true;
    @Comment(value = "Entities that are blacklisted can't be captured or released via the soul crystal, or are visible in the soul explorer")
    public List<String> blacklisted_entities = List.of("entity.minecraft.wither", "entity.minecraft.ender_dragon");

    public static WorldBridgeConfig getConfigHolder() {
        return AutoConfig.getConfigHolder(WorldBridgeConfig.class).getConfig();
    }
}
