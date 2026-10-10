package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.config.data.UnboundRegistryObjectStream;
import com.thedeathlycow.immersive.storms.client.config.section.AttributeConfigData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Biome2WeatherAttributeMap {
    private final Map<ResourceKey<Biome>, AttributeConfigData.WeatherAttributeData> biomesToAttributes = new IdentityHashMap<>();

    public AttributeConfigData.WeatherAttributeData getWeatherAttributes(final ResourceKey<Biome> biomeKey) {
        return this.biomesToAttributes.getOrDefault(biomeKey, AttributeConfigData.WeatherAttributeData.EMPTY);
    }

    public void rebind(final HolderLookup<Biome> biomes, final AttributeConfigData data) {
        List<AttributeConfigData.Entry> entries = data.entries();

        Map<ResourceKey<Biome>, WeatherAttributeDataBuilder> builders = new IdentityHashMap<>();

        for (var entry : entries) {
            UnboundRegistryObjectStream.bindToHolders(entry.biomes().stream(), biomes)
                    .distinct()
                    .map(Holder::unwrapKey)
                    .flatMap(Optional::stream)
                    .forEach(biomeKey -> {
                        WeatherAttributeDataBuilder builder = builders.computeIfAbsent(
                                biomeKey,
                                _ -> new WeatherAttributeDataBuilder()
                        );
                        builder.putAll(entry.attributes());
                    });
        }

        Map<ResourceKey<Biome>, AttributeConfigData.WeatherAttributeData> updatedCache = new IdentityHashMap<>();

        for (var builderEntry : builders.entrySet()) {
            updatedCache.putIfAbsent(builderEntry.getKey(), builderEntry.getValue().build());
        }

        this.biomesToAttributes.clear();
        this.biomesToAttributes.putAll(updatedCache);
    }

    public record WeatherAttributeDataBuilder(
            EnvironmentAttributeMap.Builder rainAttributes,
            EnvironmentAttributeMap.Builder thunderAttributes
    ) {
        public WeatherAttributeDataBuilder() {
            this(EnvironmentAttributeMap.builder(), EnvironmentAttributeMap.builder());
        }

        public WeatherAttributeDataBuilder putAll(AttributeConfigData.WeatherAttributeData data) {
            this.rainAttributes.putAll(data.rainAttributes());
            this.thunderAttributes.putAll(data.thunderAttributes());
            return this;
        }

        public AttributeConfigData.WeatherAttributeData build() {
            return new AttributeConfigData.WeatherAttributeData(this.rainAttributes.build(), this.thunderAttributes.build());
        }
    }
}