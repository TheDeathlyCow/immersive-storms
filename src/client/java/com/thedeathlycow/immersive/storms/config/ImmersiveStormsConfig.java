package com.thedeathlycow.immersive.storms.config;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersiveStormsConfig {
    public static ConfigClassHandler<ImmersiveStormsConfig> HANDLER = ConfigClassHandler.createBuilder(ImmersiveStormsConfig.class)
            .id(ImmersiveStorms.id("client"))
            .serializer(
                    config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir().resolve("my_mod.json5"))
                            .setJson5(true)
                            .build()
            )
            .build();

    @OptionName("Enable all fog changes")
    @SerialEntry(comment = "Toggle for all fog density and color changing features")
    boolean enableFogChanges = true;

    @OptionName("Enable sandstorm fog changes")
    @SerialEntry(comment = "Toggles the fog density and color changes for sandstorms")
    boolean enableSandstormFog = true;

    @OptionName("Enable blizzard fog changes")
    @SerialEntry(comment = "Toggles the fog density and color changes for blizzards")
    boolean enableBlizzardFog = true;

    @OptionName("Enable dense fog changes")
    @SerialEntry(comment = "Toggles the fog density and color changes for dense fog (affects Pale Gardens and Swamps)")
    boolean enableDenseFog = true;

    @OptionName("Enable ambient wind particles")
    @SerialEntry(comment = "Enables ambient wind particles in windy biomes")
    boolean enableAmbientWindParticles = true;

    @OptionName("Enable ambient wind sounds")
    @SerialEntry(comment = "Enables ambient wind sounds in windy biomes, has no effect in deserts/badlands due to vanilla mechanic")
    boolean enableAmbientWindSounds = true;

    @OptionName("Fog distance multiplier")
    @SerialEntry(comment = "Adjusts how close fog closes in during weather, must be positive")
    float fogDistanceMultiplier = 1.0f;

    @OptionName("Wind particle chance multiplier")
    @SerialEntry(comment = "Adjusts how often ambient wind particles appear (does not affect sandstorms), must be positive")
    float windParticleChanceMultiplier = 1.0f;

    @OptionName("Sandstorm Config")
    @NoComment
    SandstormConfig sandstorm = new SandstormConfig();

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

    public float getFogDistanceMultiplier() {
        return fogDistanceMultiplier;
    }

    public float getWindParticleChanceMultiplier() {
        return windParticleChanceMultiplier;
    }

    public SandstormConfig getSandstorm() {
        return sandstorm;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();

        if (this.fogDistanceMultiplier <= 0f) {
            throw new ValidationException("Fog distance multiplier must be positive");
        }

        if (this.windParticleChanceMultiplier <= 0f) {
            throw new ValidationException("Wind particle chance multiplier must be positive");
        }
    }
}