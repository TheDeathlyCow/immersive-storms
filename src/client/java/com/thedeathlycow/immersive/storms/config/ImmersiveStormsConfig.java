package com.thedeathlycow.immersive.storms.config;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.api.WeatherEffectType;
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
    @Comment("Toggles the fog density and color changes for dense fog (Pale Gardens)")
    @ConfigEntry.Gui.Tooltip
    boolean enableDenseFog = true;

    @OptionName("Fog distance multiplier")
    @Comment("Adjusts how close fog closes in during weather, must be positive")
    @ConfigEntry.Gui.Tooltip
    float fogDistanceMultiplier = 1.0f;

    @OptionName("Enable sandstorm particles")
    @NoComment
    boolean enableSandstormParticles = true;

    @OptionName("Sandstorm particle render distance")
    @Comment("How many blocks away to render sandstorm particles, must be positive")
    @ConfigEntry.Gui.Tooltip
    int sandstormParticleRenderDistance = 20;

    @OptionName("Sandstorm particle density")
    @Comment("Multiplier for how frequently sandstorm particles should appear, must be positive. Bigger values = more common.")
    @ConfigEntry.Gui.Tooltip
    float sandstormParticleDensityMultiplier = 1.0f;

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

    public float getFogDistanceMultiplier() {
        return fogDistanceMultiplier;
    }

    public boolean isEnableSandstormParticles() {
        return enableSandstormParticles;
    }

    public int getSandstormParticleRenderDistance() {
        return sandstormParticleRenderDistance;
    }

    public float getSandstormParticleDensityMultiplier() {
        return sandstormParticleDensityMultiplier;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        if (this.fogDistanceMultiplier <= 0f) {
            throw new ValidationException("Fog distance multiplier must be positive");
        }

        if (this.sandstormParticleRenderDistance <= 0) {
            throw new ValidationException("Sandstorm particle render distance must be positive");
        }

        if (this.sandstormParticleDensityMultiplier <= 0f) {
            throw new ValidationException("Sandstorm particle density multiplier must be positive");
        }
    }
}