package net.letscode.worldbridge.implementations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.letscode.worldbridge.WorldBridgeConfig;

public class WorldBridgeModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(WorldBridgeConfig.class, parent).get();
    }
}