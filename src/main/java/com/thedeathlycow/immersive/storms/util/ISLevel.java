package com.thedeathlycow.immersive.storms.util;

import com.thedeathlycow.immersive.storms.client.config.section.AdvancedSettings;
import com.thedeathlycow.immersive.storms.client.world.Biome2WeatherAttributeMap;
import org.jspecify.annotations.NonNull;

public interface ISLevel {
    @NonNull
    default Biome2WeatherAttributeMap immersive_storms$getBiomeToWeatherAttributeMap() {
        return this.immersive_storms$getAttributeMapHolder().immersive_storms$getBiomeToWeatherAttributeMap();
    }

    default void immersiveStorms$rebindAttributeMap(final AdvancedSettings data) {
        this.immersive_storms$getAttributeMapHolder().immersiveStorms$rebindAttributeMap(data);
    }

    AttributeMapHolder immersive_storms$getAttributeMapHolder();
}
