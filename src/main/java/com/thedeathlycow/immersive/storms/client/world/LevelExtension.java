package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.config.section.AttributeConfigData;
import org.jspecify.annotations.NonNull;

public interface LevelExtension {
    @NonNull
    Biome2WeatherAttributeMap immersive_storms$getBiomeToWeatherAttributeMap();

    void immersiveStorms$rebindAttributeMap(final AttributeConfigData data);
}
