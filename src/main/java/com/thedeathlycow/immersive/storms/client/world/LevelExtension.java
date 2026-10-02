package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.config.section.AttributeConfigData;

public interface LevelExtension {
    Biome2WeatherAttributeMap immersive_storms$getBiomeToWeatherAttributeMap();

    void immersiveStorms$rebindAttributeMap(final AttributeConfigData data);
}
