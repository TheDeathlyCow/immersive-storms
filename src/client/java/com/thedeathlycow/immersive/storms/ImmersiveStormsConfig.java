package com.thedeathlycow.immersive.storms;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = ImmersiveStorms.MOD_ID)
public class ImmersiveStormsConfig implements ConfigData {
    @Comment("Toggles the fog density changes during weather")
    @ConfigEntry.Gui.Tooltip
    boolean enableFogChanges = true;

    @Comment("Adjusts how close fog closes in during weather")
    @ConfigEntry.Gui.Tooltip
    float fogDistanceMultiplier = 1.0f;
}