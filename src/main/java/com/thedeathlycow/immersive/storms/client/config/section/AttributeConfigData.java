package com.thedeathlycow.immersive.storms.client.config.section;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.client.config.data.CodecGsonAdapter;
import com.thedeathlycow.immersive.storms.client.config.data.UnboundRegistryObjectStream;
import com.thedeathlycow.immersive.storms.client.config.data.IdentifierController;
import com.thedeathlycow.immersive.storms.client.config.Translate;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
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

    @Translate.Name("Entries")
    @SerialEntry(comment = "Maps biomes to a set of environment attribute overrides.")
    List<Entry> entries = defaultPreset();

    public List<Entry> entries() {
        return entries;
    }

    public record Entry(
            List<UnboundRegistryObjectStream<Biome>> biomes,
            WeatherAttributeData attributes
    ) {
    }

    public record WeatherAttributeData(
            EnvironmentAttributeMap rainAttributes,
            EnvironmentAttributeMap thunderAttributes
    ) {
        public static final WeatherAttributeData EMPTY = new WeatherAttributeData(EnvironmentAttributeMap.EMPTY, EnvironmentAttributeMap.EMPTY);
    }

    @FunctionalInterface
    public interface SavedCallback {
        void onSave(AttributeConfigData data);
    }

    private static List<Entry> defaultPreset() {
        // todo: conventional tags
        return new ImmutableList.Builder<Entry>()
                .add(new Entry(
                        List.of(tag(BiomeTags.IS_BADLANDS), tag(BiomeTags.IS_SAVANNA), object(Biomes.DESERT)),
                        new WeatherAttributeData(
                                EnvironmentAttributeMap.builder()
                                        .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, 0xF79523)
                                        .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, 0xF79523)
                                        .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, 128f / 1024f)
                                        .build(),
                                EnvironmentAttributeMap.builder()
                                        .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, 0xD96E38)
                                        .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, 0xD96E38)
                                        .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, 64f / 1024f)
                                        .build()
                        )
                ))
                .add(new Entry(
                        List.of(object(Biomes.PALE_GARDEN)),
                        new WeatherAttributeData(
                                EnvironmentAttributeMap.builder()
                                        .set(ISEnvironmentAttributes.HAS_BLACK_RAIN, true)
                                        .build(),
                                EnvironmentAttributeMap.builder()
                                        .set(ISEnvironmentAttributes.HAS_BLACK_RAIN, true)
                                        .build()
                        )
                ))
                .add(new Entry(
                        List.of(object(Biomes.DARK_FOREST), object(Biomes.PALE_GARDEN), object(Biomes.SWAMP), object(Biomes.MANGROVE_SWAMP)),
                        new WeatherAttributeData(
                                EnvironmentAttributeMap.builder()
                                        .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, 128f / 1024f)
                                        .build(),
                                EnvironmentAttributeMap.builder()
                                        .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, 64f / 1024f)
                                        .build()
                        )
                ))
                .build();
    }

    private static <T> UnboundRegistryObjectStream<T> object(ResourceKey<T> key) {
        return new UnboundRegistryObjectStream.Key<>(key);
    }

    private static <T> UnboundRegistryObjectStream<T> tag(TagKey<T> key) {
        return new UnboundRegistryObjectStream.Tag<>(key);
    }
}