package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.config.data.UnboundRegistryObjectStream;
import com.thedeathlycow.immersive.storms.client.config.data.WeatherBasedAttributes;
import com.thedeathlycow.immersive.storms.client.config.section.AdvancedSettings;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.WeatherAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class Biome2WeatherAttributeMap {
    private final Reference2ObjectMap<Biome, WeatherBasedAttributes> biomesToAttributes = new Reference2ObjectOpenHashMap<>();
    private final Reference2ObjectMap<Biome, EnvironmentAttributeMap> patchedClearWeatherMaps = new Reference2ObjectOpenHashMap<>();

    @NotNull
    public WeatherBasedAttributes getWeatherAttributes(final Biome biome) {
        return this.biomesToAttributes.getOrDefault(biome, WeatherBasedAttributes.EMPTY);
    }

    @NotNull
    public EnvironmentAttributeMap getClearWeatherMap(Biome biome, EnvironmentAttributeMap baseAttributes) {
        return this.patchedClearWeatherMaps.getOrDefault(biome, baseAttributes);
    }

    public void clear() {
        this.biomesToAttributes.clear();
        this.patchedClearWeatherMaps.clear();
    }

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

    public static void addDefaultLayers(
            final EnvironmentAttributeSystem.Builder builder,
            final HolderLookup<EnvironmentAttribute<?>> attributes,
            final WeatherAttributes.WeatherAccess weatherAccess,
            final AttributeAccess attributeAccess,
            final BiomeManager biomeManager
    ) {
        attributes.listElements().forEach(attribute -> addBiomeLayerForAttribute(
                builder,
                attribute.value(),
                attributeAccess,
                weatherAccess,
                biomeManager
        ));
    }

    private static <Value> void addBiomeLayerForAttribute(
            final EnvironmentAttributeSystem.Builder builder,
            final EnvironmentAttribute<Value> attribute,
            final AttributeAccess attributeAccess,
            final WeatherAttributes.WeatherAccess weatherAccess,
            final BiomeManager biomeManager
    ) {
        builder.addPositionalLayer(attribute, (result, pos, biomeWeights) -> {
            if (biomeWeights != null && attribute.isSpatiallyInterpolated()) {
                return biomeWeights.applyAttributeLayer(attribute, result);
            }

            Holder<Biome> biome = biomeManager.getNoiseBiomeAtPosition(pos.x, pos.y, pos.z);
            WeatherBasedAttributes weatherData = attributeAccess.getWeatherAttributes(biome.value());

            EnvironmentAttributeMap.Entry<Value, ?> clearValue = weatherData.clear().get(attribute);
            EnvironmentAttributeMap.Entry<Value, ?> rainValue = weatherData.rain().get(attribute);
            EnvironmentAttributeMap.Entry<Value, ?> thunderValue = weatherData.thunder().get(attribute);

            float rainLevel = weatherAccess.rainLevel();
            float thunderLevel = weatherAccess.thunderLevel();

            if (clearValue != null) {
                result = clearValue.applyModifier(result);
            }

            if (rainLevel > 0 && rainValue != null) {
                result = attribute.type().stateChangeLerp().apply(rainLevel, result, rainValue.applyModifier(result));
            }

            if (thunderLevel > 0 && thunderValue != null) {
                result = attribute.type().stateChangeLerp().apply(thunderLevel, result, thunderValue.applyModifier(result));
            }

            return result;
        });
    }


    public interface AttributeAccess {
        static AttributeAccess from(Level level) {
            return level::immersive_storms$getBiomeToWeatherAttributeMap;
        }

        Biome2WeatherAttributeMap getWeatherBasedAttributes();

        @NotNull
        default WeatherBasedAttributes getWeatherAttributes(final Biome biome) {
            return this.getWeatherBasedAttributes().getWeatherAttributes(biome);
        }

        @NotNull
        default EnvironmentAttributeMap getClearWeatherMap(Biome biome, EnvironmentAttributeMap baseAttributes) {
            return this.getWeatherBasedAttributes().getClearWeatherMap(biome, baseAttributes);
        }
    }
}