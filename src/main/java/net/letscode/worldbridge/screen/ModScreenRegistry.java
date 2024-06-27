package net.letscode.worldbridge.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.screen.custom.SoulExplorerScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenRegistry {

    public static final ScreenHandlerType<SoulExplorerScreenHandler> SOUL_EXPLORER_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            WorldBridge.id("soul_explorer"),
            new ExtendedScreenHandlerType<>(SoulExplorerScreenHandler::new)
    );

    public static void register() {

    }
}
