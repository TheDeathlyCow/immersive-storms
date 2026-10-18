package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.config.section.AdvancedSettings;
import org.jspecify.annotations.NonNull;

public interface AttributeMapHolder {
    @NonNull
    default Biome2WeatherAttributeMapView immersive_storms$getBiomeToWeatherAttributeMap() {
        return Biome2WeatherAttributeMapView.EMPTY;
    }

    default void immersive_storms$rebindAttributeMap(final AdvancedSettings data) {
        // no-op by default
    }
}
