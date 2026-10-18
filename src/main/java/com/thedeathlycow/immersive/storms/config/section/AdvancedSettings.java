package com.thedeathlycow.immersive.storms.config.section;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.client.world.WeatherArchetype;
import com.thedeathlycow.immersive.storms.compat.NSBiomes;
import com.thedeathlycow.immersive.storms.config.Translate;
import com.thedeathlycow.immersive.storms.config.data.*;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import com.thedeathlycow.immersive.storms.registry.tag.CommonBiomeTags;
import com.thedeathlycow.immersive.storms.registry.tag.ISBiomeTags;
import com.thedeathlycow.immersive.storms.sound.StormSounds;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

public class AdvancedSettings {
    public static final Path PATH = ImmersiveStorms.getConfigDir().resolve("advanced.json5");

    public static final ConfigClassHandler<AdvancedSettings> HANDLER = ConfigClassHandler.createBuilder(AdvancedSettings.class)
            .id(ImmersiveStorms.id("advanced"))
            .serializer(
                    config -> GsonConfigSerializerBuilder.create(config)
                            .appendGsonBuilder(gsonBuilder -> {
                                gsonBuilder.registerTypeAdapter(Identifier.class, new IdentifierGsonAdapter());
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

    public static final Codec<AdvancedSettings> NETWORK_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL
                            .fieldOf("edited_config")
                            .forGetter(settings -> settings.editedConfig),
                    Entry.NETWORK_CODEC.listOf()
                            .fieldOf("entries")
                            .forGetter(settings -> settings.entries)
            ).apply(instance, AdvancedSettings::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedSettings> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(AdvancedSettings.NETWORK_CODEC);

    /// Singleton instance of the default entries, allows me to edit the default values without needing to mess with
    /// user config files.
    private static final List<Entry> DEFAULT_ENTRIES = defaultPreset();

    private static final float LIGHT_FOG = 128f / 1024f;
    private static final float THICK_FOG = 64f / 1024f;
    private static final int SANDSTORM_FOG_COLOR = 0xFF9C21;
    private static final int RED_SANDSTORM_FOG_COLOR = 0xF75E05;
    private static final int PINK_SANDSTORM_FOG_COLOR = 0xE9A481;
    private static final int BLIZZARD_FOG_COLOR = 0xFFFFFF;

    @Translate.Name("Use Default Only")
    @SerialEntry(
            value = "edited_config",
            comment = """
                    This is the Immersive Storms advanced configuration file. It allows you to edit the Environment
                    Attribute values of any biome based on the weather conditions. This file is very powerful but also
                    very technical, so it is not really intended for most users to edit this directly. If you have never
                    worked with Environment Attributes before, the Minecraft wiki provides a good source of information
                    about them:
                    https://minecraft.wiki/w/Environment_attribute
                    
                    I plan to provide a GUI to edit this file in-game via in the config menu in the future. At this
                    time, this file may only be edited manually in a text editor like you are doing now.
                    
                    To apply your changes to this file, run the command "/immersive_storms reload" (requires operator
                    level 4 permission).
                    
                    If you need any help with this file feel free to join my Discord server to ask about them, or make
                    an issue on the GitHub page:
                    - https://discord.thedeathlycow.com
                    - https://github.com/TheDeathlyCow/immersive-storms/issues
                    
                    Things you may want to know:
                    - This config allows the use of either tags keys or biome IDs, tag keys must be prefixed with #.
                    - If any tag keys or biome IDs do not exist they will be simply skipped.
                    - Each of the clear, rain, and thunder fields of the weather_attributes field have the same format
                      as the attribute fields in a dimension or biome definition (except, of course, it is JSON5
                      friendly).
                    - All attribute values will be interpolated based on their state change lerp function with respect
                      to the rain/thunder level.
                    - Spatially interpolated attributes also work with this file.
                    - Modifiers from this map are applied after the default layers are built, but before non-default
                      layers (like sky flashes).
                    - The immersive-storms:visual/storm_fog_end_distance environment attribute is provided primarily for
                      compatibility and convenience reasons.
                    - This config is synchronized to clients on join or when an administrator uses the command
                      /immersive_storms reload.
                    - Changes to this file can work client-side only, unless the server has its own file to override
                      the client's with.
                    - If multiple weather_attribute maps are provided for a biome, the maps will be merged with
                      modifiers from the later map overriding those of the first.
                    
                    
                    IMPORTANT NOTE: YOU MUST FIRST SET edited_config TO true TO ALLOW FOR THIS FILE HAVE ANY EFFECT!
                    This is so that I can freely edit the default values for users who choose not to edit this file and
                    have the changes take effect without needing to mess with your edits.
                    """
    )
    boolean editedConfig = false;

    @Translate.Name("Entries")
    @Translate.NoComment
    @SerialEntry
    List<Entry> entries = DEFAULT_ENTRIES;

    public AdvancedSettings() {

    }

    private AdvancedSettings(boolean editedConfig, List<Entry> entries) {
        this.editedConfig = editedConfig;
        this.entries = entries;
    }

    public List<Entry> entries() {
        return editedConfig ? entries : DEFAULT_ENTRIES;
    }

    public record Entry(
            List<UnboundRegistryObjectStream<Biome>> biomes,
            WeatherBasedAttributes weatherAttributes
    ) {
        public static final Codec<Entry> NETWORK_CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        UnboundRegistryObjectStream.codec(Registries.BIOME).listOf()
                                .fieldOf("biomes")
                                .forGetter(Entry::biomes),
                        WeatherBasedAttributes.NETWORK_CODEC
                                .fieldOf("weather_attributes")
                                .forGetter(Entry::weatherAttributes)
                ).apply(instance, Entry::new)
        );
    }

    private static List<Entry> defaultPreset() {
        return new ImmutableList.Builder<Entry>()
                .add(new Entry(
                        windyBiomes().build(),
                        WeatherBasedAttributes.builder()
                                .setAllWeather(ISEnvironmentAttributes.HAS_AMBIENT_WIND, true)
                                .build()
                ))
                .add(new Entry(
                        blackRainBiomes().build(),
                        WeatherBasedAttributes.builder()
                                .set(ISEnvironmentAttributes.HAS_BLACK_RAIN, true, WeatherCondition.RAINING)
                                .set(ISEnvironmentAttributes.HAS_BLACK_RAIN, true, WeatherCondition.THUNDERING)
                                .build()
                ))
                .add(new Entry(
                        denseFogBiomes().build(),
                        WeatherBasedAttributes.builder()
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, LIGHT_FOG, WeatherCondition.RAINING)
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, THICK_FOG, WeatherCondition.THUNDERING)
                                .setAllWeather(ISEnvironmentAttributes.WEATHER_ARCHETYPE, WeatherArchetype.SPOOKY)
                                .build()
                ))
                .add(new Entry(
                        sandstormBiomes().build(),
                        WeatherBasedAttributes.builder()
                                .setAllWeather(ISEnvironmentAttributes.HAS_SANDSTORM_DUST, true)
                                .setAllWeather(ISEnvironmentAttributes.WEATHER_ARCHETYPE, WeatherArchetype.SANDSTORM)
                                // rain
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, SANDSTORM_FOG_COLOR, WeatherCondition.RAINING)
                                .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, SANDSTORM_FOG_COLOR, WeatherCondition.RAINING)
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, LIGHT_FOG, WeatherCondition.RAINING)
                                .set(ISEnvironmentAttributes.STORM_SOUNDS, StormSounds.of(ISSoundEvents.WEATHER_SOFT_WIND), WeatherCondition.RAINING)
                                // thunder
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, SANDSTORM_FOG_COLOR, WeatherCondition.THUNDERING)
                                .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, SANDSTORM_FOG_COLOR, WeatherCondition.THUNDERING)
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, THICK_FOG, WeatherCondition.THUNDERING)
                                .set(ISEnvironmentAttributes.STORM_SOUNDS, StormSounds.of(ISSoundEvents.WEATHER_STRONG_WIND), WeatherCondition.THUNDERING)
                                .build()
                ))
                .add(new Entry(
                        redSandstormBiomes().build(),
                        WeatherBasedAttributes.builder()
                                // rain
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, RED_SANDSTORM_FOG_COLOR, WeatherCondition.RAINING)
                                .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, RED_SANDSTORM_FOG_COLOR, WeatherCondition.RAINING)
                                // thunder
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, RED_SANDSTORM_FOG_COLOR, WeatherCondition.THUNDERING)
                                .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, RED_SANDSTORM_FOG_COLOR, WeatherCondition.THUNDERING)
                                .build()
                ))
                .add(new Entry(
                        pinkSandstormBiomes().build(),
                        WeatherBasedAttributes.builder()
                                // rain
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, PINK_SANDSTORM_FOG_COLOR, WeatherCondition.RAINING)
                                .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, PINK_SANDSTORM_FOG_COLOR, WeatherCondition.RAINING)
                                // thunder
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, PINK_SANDSTORM_FOG_COLOR, WeatherCondition.THUNDERING)
                                .modify(EnvironmentAttributes.SKY_COLOR, ColorModifier.MULTIPLY_RGB, PINK_SANDSTORM_FOG_COLOR, WeatherCondition.THUNDERING)
                                .build()
                ))
                .add(new Entry(
                        blizzardBiomes().build(),
                        WeatherBasedAttributes.builder()
                                .setAllWeather(ISEnvironmentAttributes.WEATHER_ARCHETYPE, WeatherArchetype.BLIZZARD)
                                .modify(EnvironmentAttributes.FOG_COLOR, ColorModifier.MULTIPLY_RGB, BLIZZARD_FOG_COLOR, WeatherCondition.THUNDERING)
                                .set(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, LIGHT_FOG, WeatherCondition.THUNDERING)
                                .set(ISEnvironmentAttributes.STORM_SOUNDS, StormSounds.of(ISSoundEvents.WEATHER_SOFT_WIND), WeatherCondition.THUNDERING)
                                .build()
                ))
                .build();
    }

    private static ImmutableList.Builder<UnboundRegistryObjectStream<Biome>> pinkSandstormBiomes() {
        return ImmutableList.<UnboundRegistryObjectStream<Biome>>builder()
                .add(object(NSBiomes.DRYLANDS))
                .add(object(NSBiomes.WOODED_DRYLANDS));
    }

    private static ImmutableList.Builder<UnboundRegistryObjectStream<Biome>> redSandstormBiomes() {
        return ImmutableList.<UnboundRegistryObjectStream<Biome>>builder()
                .add(tag(BiomeTags.IS_BADLANDS))
                .add(tag(CommonBiomeTags.IS_BADLANDS));
    }

    private static ImmutableList.Builder<UnboundRegistryObjectStream<Biome>> sandstormBiomes() {
        return ImmutableList.<UnboundRegistryObjectStream<Biome>>builder()
                .addAll(redSandstormBiomes().build())
                .addAll(pinkSandstormBiomes().build())
                .add(tag(ISBiomeTags.HAS_SANDSTORMS))
                .add(tag(BiomeTags.IS_SAVANNA))
                .add(tag(CommonBiomeTags.IS_DESERT))
                .add(tag(CommonBiomeTags.IS_SAVANNA))
                .add(object(Biomes.DESERT));
    }

    private static ImmutableList.Builder<UnboundRegistryObjectStream<Biome>> blackRainBiomes() {
        return ImmutableList.<UnboundRegistryObjectStream<Biome>>builder()
                .add(tag(ISBiomeTags.IS_BLACK_RAIN_AFFECTED))
                .add(tag(CommonBiomeTags.PRIMARY_WOOD_TYPE_PALE_OAK))
                .add(tag(CommonBiomeTags.IS_NETHER))
                .add(tag(CommonBiomeTags.IS_END))
                .add(tag(BiomeTags.IS_NETHER))
                .add(tag(BiomeTags.IS_END))
                .add(object(Biomes.PALE_GARDEN));
    }

    private static ImmutableList.Builder<UnboundRegistryObjectStream<Biome>> denseFogBiomes() {
        return blackRainBiomes()
                .add(tag(ISBiomeTags.HAS_DENSE_FOG))
                .add(tag(CommonBiomeTags.IS_SPOOKY))
                .add(tag(CommonBiomeTags.IS_SWAMP))
                .add(object(Biomes.DEEP_DARK))
                .add(object(Biomes.DARK_FOREST))
                .add(object(Biomes.SWAMP))
                .add(object(Biomes.MANGROVE_SWAMP));
    }

    private static ImmutableList.Builder<UnboundRegistryObjectStream<Biome>> windyBiomes() {
        return sandstormBiomes()
                .add(tag(BiomeTags.IS_MOUNTAIN))
                .add(object(Biomes.WINDSWEPT_FOREST))
                .add(object(Biomes.WINDSWEPT_HILLS))
                .add(object(Biomes.WINDSWEPT_GRAVELLY_HILLS))
                .add(object(Biomes.WINDSWEPT_SAVANNA));
    }

    private static ImmutableList.Builder<UnboundRegistryObjectStream<Biome>> blizzardBiomes() {
        return ImmutableList.<UnboundRegistryObjectStream<Biome>>builder()
                .add(tag(ISBiomeTags.HAS_BLIZZARDS))
                .add(tag(CommonBiomeTags.IS_ICY))
                .add(tag(CommonBiomeTags.IS_AQUATIC_ICY))
                .add(tag(CommonBiomeTags.IS_SNOWY))
                .add(object(Biomes.SNOWY_PLAINS))
                .add(object(Biomes.SNOWY_BEACH))
                .add(object(Biomes.SNOWY_TAIGA))
                .add(object(Biomes.SNOWY_SLOPES))
                .add(object(Biomes.JAGGED_PEAKS))
                .add(object(Biomes.FROZEN_OCEAN))
                .add(object(Biomes.FROZEN_PEAKS))
                .add(object(Biomes.DEEP_FROZEN_OCEAN))
                .add(object(Biomes.FROZEN_RIVER))
                .add(object(Biomes.GROVE))
                .add(object(Biomes.SNOWY_BEACH));
    }

    private static <T> UnboundRegistryObjectStream<T> object(ResourceKey<T> key) {
        return new UnboundRegistryObjectStream.Key<>(key);
    }

    private static <T> UnboundRegistryObjectStream<T> tag(TagKey<T> key) {
        return new UnboundRegistryObjectStream.Tag<>(key);
    }
}