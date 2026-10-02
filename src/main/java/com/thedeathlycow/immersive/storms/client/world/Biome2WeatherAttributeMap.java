package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.client.config.data.UnboundRegistryObjectStream;
import com.thedeathlycow.immersive.storms.client.config.section.AttributeConfigData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
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
        Map<ResourceKey<Biome>, AttributeConfigData.WeatherAttributeData> updatedCache = new IdentityHashMap<>();

        for (var entry : entries) {
            UnboundRegistryObjectStream.bindToHolders(entry.biomes().stream(), biomes)
                    .distinct()
                    .map(Holder::unwrapKey)
                    .flatMap(Optional::stream)
                    .forEach(biomeKey -> {
                        if (updatedCache.putIfAbsent(biomeKey, entry.attributes()) != null) {
                            ImmersiveStorms.LOGGER.warn("Biome {} has duplicate attribute overrides!", biomeKey.identifier());
                        }
                    });
        }

        this.biomesToAttributes.putAll(updatedCache);
    }
}