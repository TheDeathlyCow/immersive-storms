package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.config.data.WeatherBasedAttributes;
import com.thedeathlycow.immersive.storms.config.section.AdvancedSettings;
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

public interface Biome2WeatherAttributeMapView {
    Biome2WeatherAttributeMapView EMPTY = new Biome2WeatherAttributeMapView() {
        @Override
        public @NotNull WeatherBasedAttributes getWeatherAttributes(Biome biome) {
            return WeatherBasedAttributes.EMPTY;
        }

        @Override
        public @NotNull EnvironmentAttributeMap getClearWeatherMap(Biome biome, EnvironmentAttributeMap baseAttributes) {
            return WeatherBasedAttributes.EMPTY.clear();
        }

        @Override
        public void clear() {
            // no-op
        }

        @Override
        public void rebind(HolderLookup<Biome> biomes, AdvancedSettings data) {
            // no-op
        }
    };

    @NotNull
    WeatherBasedAttributes getWeatherAttributes(final Biome biome);

    @NotNull
    EnvironmentAttributeMap getClearWeatherMap(Biome biome, EnvironmentAttributeMap baseAttributes);

    void clear();

    void rebind(final HolderLookup<Biome> biomes, final AdvancedSettings data);

    static void addDefaultLayers(
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

        Biome2WeatherAttributeMapView getWeatherBasedAttributes();

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