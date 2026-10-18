package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.config.data.UnboundRegistryObjectStream;
import com.thedeathlycow.immersive.storms.config.data.WeatherBasedAttributes;
import com.thedeathlycow.immersive.storms.config.section.AdvancedSettings;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class Biome2WeatherAttributeMapImpl implements Biome2WeatherAttributeMapView {
    private final Reference2ObjectMap<Biome, WeatherBasedAttributes> biomesToAttributes = new Reference2ObjectOpenHashMap<>();
    private final Reference2ObjectMap<Biome, EnvironmentAttributeMap> patchedClearWeatherMaps = new Reference2ObjectOpenHashMap<>();

    @NotNull
    @Override
    public WeatherBasedAttributes getWeatherAttributes(final Biome biome) {
        return this.biomesToAttributes.getOrDefault(biome, WeatherBasedAttributes.EMPTY);
    }

    @NotNull
    @Override
    public EnvironmentAttributeMap getClearWeatherMap(Biome biome, EnvironmentAttributeMap baseAttributes) {
        return this.patchedClearWeatherMaps.getOrDefault(biome, baseAttributes);
    }

    @Override
    public void clear() {
        this.biomesToAttributes.clear();
        this.patchedClearWeatherMaps.clear();
    }

    @Override
    public void rebind(final HolderLookup<Biome> biomes, final AdvancedSettings data) {
        List<AdvancedSettings.Entry> entries = data.entries();

        Reference2ObjectMap<Biome, WeatherBasedAttributes.Builder> builders = new Reference2ObjectOpenHashMap<>();
        Reference2ObjectMap<Biome, EnvironmentAttributeMap> clearWeatherMaps = new Reference2ObjectOpenHashMap<>();

        for (AdvancedSettings.Entry entry : entries) {
            UnboundRegistryObjectStream.bindToHolders(entry.biomes().stream(), biomes)
                    .distinct()
                    .forEach(holder -> {
                        Biome biome = holder.value();
                        WeatherBasedAttributes weatherAttributes = entry.weatherAttributes();

                        WeatherBasedAttributes.Builder builder = builders.computeIfAbsent(
                                biome,
                                _ -> WeatherBasedAttributes.builder()
                        );
                        builder.putInAllWeather(weatherAttributes);

                        clearWeatherMaps.computeIfAbsent(biome, (Biome b) -> merge(b.getAttributes(), weatherAttributes.clear()));
                    });
        }

        Reference2ObjectMap<Biome, WeatherBasedAttributes> updatedCache = new Reference2ObjectOpenHashMap<>();

        for (var builderEntry : builders.entrySet()) {
            updatedCache.putIfAbsent(builderEntry.getKey(), builderEntry.getValue().build());
        }

        this.clear();
        this.biomesToAttributes.putAll(updatedCache);
        this.patchedClearWeatherMaps.putAll(clearWeatherMaps);
    }

    private static EnvironmentAttributeMap merge(final EnvironmentAttributeMap first, final EnvironmentAttributeMap second) {
        EnvironmentAttributeMap.Builder builder = EnvironmentAttributeMap.builder();
        builder.putAll(first);
        builder.putAll(second);
        return builder.build();
    }
}