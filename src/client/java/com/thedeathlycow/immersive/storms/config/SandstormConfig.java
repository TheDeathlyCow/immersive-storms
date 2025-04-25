package com.thedeathlycow.immersive.storms.config;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = ImmersiveStorms.MOD_ID + ".sandstorm")
public class SandstormConfig implements ConfigData {
    @OptionName("Enable sandstorm particles")
    @NoComment
    boolean enableSandstormParticles = true;

    @OptionName("Enable sandstorm sounds")
    @NoComment
    boolean enableSandstormSounds = true;

    @OptionName("Detect Particle Rain")
    @Comment("Will disable sandstorm particles and sounds automatically if the mod Particle Rain is detected")
    boolean detectParticleRain = true;

    @OptionName("Sandstorm particle render distance")
    @Comment("How many blocks away to render sandstorm particles, must be positive")
    @ConfigEntry.Gui.Tooltip
    int sandstormParticleRenderDistance = 20;

    @OptionName("Sandstorm particle density")
    @Comment("Multiplier for how frequently sandstorm particles should appear, must be positive. Bigger values = more common.")
    @ConfigEntry.Gui.Tooltip
    float sandstormParticleDensityMultiplier = 1.0f;

    public boolean isEnableSandstormParticles() {
        return enableSandstormParticles;
    }

    public boolean isEnableSandstormSounds() {
        return enableSandstormSounds;
    }

    public boolean isDetectParticleRain() {
        return detectParticleRain;
    }

    public int getSandstormParticleRenderDistance() {
        return sandstormParticleRenderDistance;
    }

    public float getSandstormParticleDensityMultiplier() {
        return sandstormParticleDensityMultiplier;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        if (this.sandstormParticleRenderDistance <= 0) {
            throw new ValidationException("Sandstorm particle render distance must be positive");
        }

        if (this.sandstormParticleDensityMultiplier <= 0f) {
            throw new ValidationException("Sandstorm particle density multiplier must be positive");
        }
    }
}