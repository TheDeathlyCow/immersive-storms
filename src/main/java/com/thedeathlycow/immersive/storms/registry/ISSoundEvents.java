package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public final class ISSoundEvents {
    public static final SoundEvent WEATHER_STRONG_WIND = register("weather.strong_wind");

    public static void initialize() {
        ImmersiveStorms.LOGGER.debug("Initialized Immersive Storms sound events");
    }

    private static SoundEvent register(String name) {
        SoundEvent event = SoundEvent.createVariableRangeEvent(ImmersiveStorms.id(name));

        return Registry.register(BuiltInRegistries.SOUND_EVENT, event.location(), event);
    }

    private ISSoundEvents() {

    }
}