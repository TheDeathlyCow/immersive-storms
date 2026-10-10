package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.config.data.UnboundRegistryObjectStream;
import com.thedeathlycow.immersive.storms.client.config.section.AttributeConfigData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.WeatherAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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
            ResourceKey<Biome> biomeKey = biome.unwrapKey().orElseThrow();
            AttributeConfigData.WeatherAttributeData data = attributeAccess.getWeatherBasedAttributes()
                    .getWeatherAttributes(biomeKey);

            EnvironmentAttributeMap.Entry<Value, ?> rainValue = data.rainAttributes().get(attribute);
            EnvironmentAttributeMap.Entry<Value, ?> thunderValue = data.thunderAttributes().get(attribute);

            float rainLevel = weatherAccess.rainLevel();
            float thunderLevel = weatherAccess.thunderLevel();

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
            return ((LevelExtension) level)::immersive_storms$getBiomeToWeatherAttributeMap;
        }

        Biome2WeatherAttributeMap getWeatherBasedAttributes();
    }
}