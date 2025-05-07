package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public final class ISSoundEvents {
    public static final SoundEvent WEATHER_STRONG_WIND = register("weather.strong_wind");

    public static void initialize() {
        ImmersiveStorms.LOGGER.debug("Initialized Immersive Storms sound events");
    }

    private static SoundEvent register(String name) {
        SoundEvent event = SoundEvent.of(ImmersiveStorms.id(name));

        return Registry.register(Registries.SOUND_EVENT, event.id(), event);
    }

    private ISSoundEvents() {

    }
}