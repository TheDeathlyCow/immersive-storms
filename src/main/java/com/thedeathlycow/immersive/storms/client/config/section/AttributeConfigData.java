package com.thedeathlycow.immersive.storms.client.config.section;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.client.config.Translate;
import com.thedeathlycow.immersive.storms.client.config.data.*;
import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import com.thedeathlycow.immersive.storms.sound.StormSounds;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.yumi.commons.event.Event;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.attribute.modifier.ColorModifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

public class AttributeConfigData {
    public static final Path PATH = ImmersiveStorms.getConfigDir().resolve("attribute_data.json5");

    public static final Event<Identifier, SavedCallback> ON_SAVED = ImmersiveStorms.EVENT_MANAGER.create(
            SavedCallback.class,
            listeners -> data -> {
                for (SavedCallback listener : listeners) {
                    listener.onSave(data);
                }
            }
    );

    public static final ConfigClassHandler<AttributeConfigData> HANDLER = ConfigClassHandler.createBuilder(AttributeConfigData.class)
            .id(ImmersiveStorms.id("attribute_data"))
            .serializer(
                    config -> GsonConfigSerializerBuilder.create(config)
                            .appendGsonBuilder(gsonBuilder -> {
                                IdentifierController.registerTypeAdapter(gsonBuilder);
                                CodecGsonAdapter.registerTypeAdapter(gsonBuilder, EnvironmentAttributeMap.class, EnvironmentAttributeMap.CODEC);

                                Type biomeEntryType = new TypeToken<UnboundRegistryObjectStream<Biome>>() {
                                }.getType();
                                CodecGsonAdapter.registerTypeAdapter(gsonBuilder, biomeEntryType, UnboundRegistryObjectStream.codec(Registries.BIOME));

                                return gsonBuilder;
                            })
                            .setPath(PATH)
                            .setJson5(true)
                            .build()
            )
            .build();

    private static final float LIGHT_FOG = 128f / 1024f;

    private static final float THICK_FOG = 64f / 1024f;

    @Translate.Name("Entries")
    @SerialEntry(comment = "Maps biomes to a set of environment attribute overrides.")
    List<Entry> entries = defaultPreset();

    public List<Entry> entries() {
        return entries;
    }

    public record Entry(
            List<UnboundRegistryObjectStream<Biome>> biomes,
            WeatherBasedAttributes attributes
    ) {
    }

    @FunctionalInterface
    public interface SavedCallback {
        void onSave(AttributeConfigData data);
    }

    private static List<Entry> defaultPreset() {
        // todo: conventional tags
        return new ImmutableList.Builder<Entry>()
                .add(new Entry(
                        sandstormBiomes(),
                        WeatherBasedAttributes.builder()
                                .setAllWeather(ISEnvironmentAttributes.HAS_SANDSTORM_DUST, true)
                                // rain
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, 0xF79523, WeatherCondition.RAINING)
                                .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, 0xF79523, WeatherCondition.RAINING)
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, LIGHT_FOG, WeatherCondition.RAINING)
                                .set(ISEnvironmentAttributes.STORM_SOUNDS, StormSounds.of(ISSoundEvents.WEATHER_SOFT_WIND), WeatherCondition.RAINING)
                                // thunder
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, 0xD96E38, WeatherCondition.THUNDERING)
                                .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, 0xD96E38, WeatherCondition.THUNDERING)
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, THICK_FOG, WeatherCondition.THUNDERING)
                                .set(ISEnvironmentAttributes.STORM_SOUNDS, StormSounds.of(ISSoundEvents.WEATHER_STRONG_WIND), WeatherCondition.THUNDERING)
                                .build()
                ))
                .add(new Entry(
                        blackRainBiomes(),
                        WeatherBasedAttributes.builder()
                                .set(ISEnvironmentAttributes.HAS_BLACK_RAIN, true, WeatherCondition.RAINING)
                                .set(ISEnvironmentAttributes.HAS_BLACK_RAIN, true, WeatherCondition.THUNDERING)
                                .build()
                ))
                .add(new Entry(
                        denseFogBiomes(),
                        WeatherBasedAttributes.builder()
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, LIGHT_FOG, WeatherCondition.RAINING)
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, THICK_FOG, WeatherCondition.THUNDERING)
                                .build()
                ))
                .add(new Entry(
                        windyBiomes(),
                        WeatherBasedAttributes.builder()
                                .setAllWeather(ISEnvironmentAttributes.HAS_AMBIENT_WIND, true)
                                .build()
                ))
                .add(new Entry(
                        blizzardBiomes(),
                        WeatherBasedAttributes.builder()
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, 0xFFFFFF, WeatherCondition.THUNDERING)
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, LIGHT_FOG, WeatherCondition.THUNDERING)
                                .set(ISEnvironmentAttributes.STORM_SOUNDS, StormSounds.of(ISSoundEvents.WEATHER_SOFT_WIND), WeatherCondition.THUNDERING)
                                .build()
                ))
                .build();
    }

    private static List<UnboundRegistryObjectStream<Biome>> sandstormBiomes() {
        return List.of(tag(BiomeTags.IS_BADLANDS), tag(BiomeTags.IS_SAVANNA), object(Biomes.DESERT));
    }

    private static List<UnboundRegistryObjectStream<Biome>> blackRainBiomes() {
        return List.of(object(Biomes.PALE_GARDEN));
    }

    private static List<UnboundRegistryObjectStream<Biome>> denseFogBiomes() {
        return List.of(object(Biomes.DARK_FOREST), object(Biomes.PALE_GARDEN), object(Biomes.SWAMP), object(Biomes.MANGROVE_SWAMP));
    }

    private static List<UnboundRegistryObjectStream<Biome>> windyBiomes() {
        return List.of(tag(BiomeTags.IS_MOUNTAIN), tag(BiomeTags.IS_BADLANDS), tag(BiomeTags.IS_SAVANNA), object(Biomes.DESERT), object(Biomes.WINDSWEPT_FOREST), object(Biomes.WINDSWEPT_HILLS), object(Biomes.WINDSWEPT_GRAVELLY_HILLS), object(Biomes.WINDSWEPT_SAVANNA));
    }

    private static List<UnboundRegistryObjectStream<Biome>> blizzardBiomes() {
        return List.of(tag(ISBiomeTags.HAS_BLIZZARDS));
    }

    private static <T> UnboundRegistryObjectStream<T> object(ResourceKey<T> key) {
        return new UnboundRegistryObjectStream.Key<>(key);
    }

    private static <T> UnboundRegistryObjectStream<T> tag(TagKey<T> key) {
        return new UnboundRegistryObjectStream.Tag<>(key);
    }
}