package com.thedeathlycow.immersive.storms.config;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = ImmersiveStorms.MOD_ID)
public class ImmersiveStormsConfig implements ConfigData {
    @OptionName("Enable fog changes")
    @Comment("Toggles the fog density changes during weather")
    @ConfigEntry.Gui.Tooltip
    boolean enableFogChanges = true;

    @OptionName("Fog distance multiplier")
    @Comment("Adjusts how close fog closes in during weather")
    @ConfigEntry.Gui.Tooltip
    float fogDistanceMultiplier = 1.0f;

    public boolean isEnableFogChanges() {
        return enableFogChanges;
    }

    public float getFogDistanceMultiplier() {
        return fogDistanceMultiplier;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        if (this.fogDistanceMultiplier <= 0f) {
            throw new ValidationException("Fog distance multiplier must be positive");
        }
    }
}