package com.thedeathlycow.immersive.storms.config;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = ImmersiveStorms.MOD_ID)
public class ImmersiveStormsConfig implements ConfigData {
    @OptionName("Enable all fog changes")
    @Comment("Toggle for all fog density and color changing features")
    @ConfigEntry.Gui.Tooltip
    boolean enableFogChanges = true;

    @OptionName("Enable sandstorm fog changes")
    @Comment("Toggles the fog density and color changes for sandstorms")
    @ConfigEntry.Gui.Tooltip
    boolean enableSandstormFog = true;

    @OptionName("Enable blizzard fog changes")
    @Comment("Toggles the fog density and color changes for blizzards")
    @ConfigEntry.Gui.Tooltip
    boolean enableBlizzardFog = true;

    @OptionName("Enable dense fog changes")
    @Comment("Toggles the fog density and color changes for dense fog (affects Pale Gardens and Swamps)")
    @ConfigEntry.Gui.Tooltip
    boolean enableDenseFog = true;

    @OptionName("Enable ambient wind particles")
    @Comment("Enables ambient wind particles in windy biomes")
    @ConfigEntry.Gui.Tooltip
    boolean enableAmbientWindParticles = true;

    @OptionName("Enable ambient wind sounds")
    @Comment("Enables ambient wind sounds in windy biomes, has no effect in deserts/badlands due to vanilla mechanic")
    @ConfigEntry.Gui.Tooltip
    boolean enableAmbientWindSounds = true;

    @OptionName("Fog distance multiplier")
    @Comment("Adjusts how close fog closes in during weather, must be positive")
    @ConfigEntry.Gui.Tooltip
    float fogDistanceMultiplier = 1.0f;

    @OptionName("Sandstorm Config")
    @NoComment
    @ConfigEntry.Gui.CollapsibleObject
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

    public SandstormConfig getSandstorm() {
        return sandstorm;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();

        if (this.fogDistanceMultiplier <= 0f) {
            throw new ValidationException("Fog distance multiplier must be positive");
        }
    }
}