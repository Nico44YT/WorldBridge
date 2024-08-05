package net.letscode.worldbridge.sound;

import net.letscode.worldbridge.WorldBridge;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundRegistry {
    public static final SoundEvent RELEASE_SOUND = registerSound(WorldBridge.id("release"));

    public static void register() {

    }

    private static SoundEvent registerSound(Identifier identifier) {
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }
}
