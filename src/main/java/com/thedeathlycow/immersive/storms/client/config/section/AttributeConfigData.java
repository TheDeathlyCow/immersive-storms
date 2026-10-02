package com.thedeathlycow.immersive.storms.client.config.section;

import com.google.gson.reflect.TypeToken;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.client.config.data.CodecGsonAdapter;
import com.thedeathlycow.immersive.storms.client.config.data.UnboundRegistryObjectStream;
import com.thedeathlycow.immersive.storms.client.config.data.IdentifierController;
import com.thedeathlycow.immersive.storms.client.config.Translate;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.yumi.commons.event.Event;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;

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

                                Type biomeEntryType = new TypeToken<UnboundRegistryObjectStream<Biome>>() {}.getType();
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
    List<Entry> entries;

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
}