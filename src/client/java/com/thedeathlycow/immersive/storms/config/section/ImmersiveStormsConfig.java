package com.thedeathlycow.immersive.storms.config.section;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.config.Translate;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.FloatSlider;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;

import java.nio.file.Path;

public class ImmersiveStormsConfig {
    public static final Path PATH = ImmersiveStorms.getConfigDir().resolve("general.json5");

    public static final ConfigClassHandler<ImmersiveStormsConfig> HANDLER = ConfigClassHandler.createBuilder(ImmersiveStormsConfig.class)
            .id(ImmersiveStorms.id("general"))
            .serializer(
                    config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(PATH)
                            .setJson5(true)
                            .build()
            )
            .build();

    private static final String GENERAL_CATEGORY = "general";

    @AutoGen(category = GENERAL_CATEGORY)
    @Translate.Name("Enable all fog changes")
    @TickBox
    @SerialEntry(comment = "Toggle for all fog density and color changing features")
    boolean enableFogChanges = true;

    @AutoGen(category = GENERAL_CATEGORY)
    @Translate.Name("Enable sandstorm fog changes")
    @TickBox
    @SerialEntry(comment = "Toggles the fog density and color changes for sandstorms")
    boolean enableSandstormFog = true;

    @AutoGen(category = GENERAL_CATEGORY)
    @Translate.Name("Enable blizzard fog changes")
    @TickBox
    @SerialEntry(comment = "Toggles the fog density and color changes for blizzards")
    boolean enableBlizzardFog = true;

    @AutoGen(category = GENERAL_CATEGORY)
    @Translate.Name("Enable dense fog changes")
    @TickBox
    @SerialEntry(comment = "Toggles the fog density and color changes for dense fog (affects Pale Gardens and Swamps)")
    boolean enableDenseFog = true;

    @AutoGen(category = GENERAL_CATEGORY)
    @Translate.Name("Enable ambient wind particles")
    @TickBox
    @SerialEntry(comment = "Enables ambient wind particles in windy biomes")
    boolean enableAmbientWindParticles = true;

    @AutoGen(category = GENERAL_CATEGORY)
    @Translate.Name("Enable ambient wind sounds")
    @TickBox
    @SerialEntry(comment = "Enables ambient wind sounds in windy biomes, has no effect in deserts/badlands due to vanilla mechanic")
    boolean enableAmbientWindSounds = true;

    @AutoGen(category = GENERAL_CATEGORY)
    @Translate.Name("Enable strong wind sounds")
    @TickBox
    @SerialEntry(comment = "Enables the strong wind sounds from sandstorms and blizzards")
    boolean enableStrongWindSounds = true;

    @AutoGen(category = GENERAL_CATEGORY)
    @Translate.Name("Fog distance multiplier")
    @FloatSlider(min = 0.1f, max = 10.0f, step = 0.1f, format = "%.2f")
    @SerialEntry(comment = "Adjusts how close fog closes in during weather, must be positive")
    float fogDistanceMultiplier = 1.0f;

    @AutoGen(category = GENERAL_CATEGORY)
    @Translate.Name("Wind particle chance multiplier")
    @FloatSlider(min = 0.1f, max = 10.0f, step = 0.1f, format = "%.2f")
    @SerialEntry(comment = "Adjusts how often ambient wind particles appear (does not affect sandstorms), must be positive")
    float windParticleChanceMultiplier = 1.0f;

    public boolean isEnableFogChanges() {
        return enableFogChanges;
    }

    public boolean isEnabled(WeatherEffectType type) {
        return switch (type) {
            case SANDSTORM -> this.enableSandstormFog;
            case BLIZZARD -> this.enableBlizzardFog;
            case DENSE_FOG -> this.enableDenseFog;
            case NONE -> false;
            case null -> false;
        };
    }

    public boolean isEnableAmbientWindParticles() {
        return enableAmbientWindParticles;
    }

    public boolean isEnableAmbientWindSounds() {
        return enableAmbientWindSounds;
    }

    public boolean isEnableStrongWindSounds() {
        return enableStrongWindSounds;
    }

    public float getFogDistanceMultiplier() {
        return fogDistanceMultiplier;
    }

    public float getWindParticleChanceMultiplier() {
        return windParticleChanceMultiplier;
    }

    @Deprecated
    public SandstormConfig getSandstorm() {
        return SandstormConfig.HANDLER.instance();
    }
}