package net.letscode.worldbridge;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "worldbridge")
public class WorldBridgeConfig implements ConfigData {

    public boolean allow_transfer_between_worlds = false;
    public boolean allow_inventory_transfer = false;
    public boolean only_uploader_access = true;

    public static WorldBridgeConfig getConfigHolder() {
        return AutoConfig.getConfigHolder(WorldBridgeConfig.class).getConfig();
    }
}
