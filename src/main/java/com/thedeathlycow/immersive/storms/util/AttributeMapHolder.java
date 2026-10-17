package com.thedeathlycow.immersive.storms.util;

import com.thedeathlycow.immersive.storms.client.config.section.AdvancedSettings;
import com.thedeathlycow.immersive.storms.client.world.Biome2WeatherAttributeMap;
import org.jspecify.annotations.NonNull;

public interface AttributeMapHolder {
    AttributeMapHolder EMPTY = new AttributeMapHolder() {
        private final Biome2WeatherAttributeMap map = new Biome2WeatherAttributeMap();

        @Override
        public @NonNull Biome2WeatherAttributeMap immersive_storms$getBiomeToWeatherAttributeMap() {
            return this.map;
        }

        @Override
        public void immersiveStorms$rebindAttributeMap(AdvancedSettings data) {
            // noop
        }
    };

    @NonNull
    Biome2WeatherAttributeMap immersive_storms$getBiomeToWeatherAttributeMap();

    void immersiveStorms$rebindAttributeMap(final AdvancedSettings data);
}
