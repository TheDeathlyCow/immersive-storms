package com.thedeathlycow.immersive.storms.config;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.FloatSlider;
import dev.isxander.yacl3.config.v2.api.autogen.IntSlider;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;

import java.nio.file.Path;

public class SandstormConfig {
    static final Path PATH = ImmersiveStorms.getConfigDir().resolve("sandstorm.json5");

    public static final ConfigClassHandler<SandstormConfig> HANDLER = ConfigClassHandler.createBuilder(SandstormConfig.class)
            .id(ImmersiveStorms.id("sandstorm"))
            .serializer(
                    config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(PATH)
                            .setJson5(true)
                            .build()
            )
            .build();

    private static final String CATEGORY = "sandstorms";

    @AutoGen(category = CATEGORY)
    @Translate.Name("Enable sandstorm particles")
    @Translate.NoComment
    @TickBox
    @SerialEntry
    boolean enableSandstormParticles = true;

    @AutoGen(category = CATEGORY)
    @Translate.Name("Enable sandstorm sounds")
    @Translate.NoComment
    @TickBox
    @SerialEntry
    boolean enableSandstormSounds = true;

    @AutoGen(category = CATEGORY)
    @Translate.Name("Detect Particle Rain")
    @TickBox
    @SerialEntry(comment = "Will disable sandstorm particles and sounds automatically if the mod Particle Rain is detected")
    boolean detectParticleRain = true;

    @AutoGen(category = CATEGORY)
    @Translate.Name("Sandstorm particle render distance")
    @IntSlider(min = 1, max = 100, step = 1)
    @SerialEntry(comment = "How many blocks away to render sandstorm particles, must be positive")
    int sandstormParticleRenderDistance = 20;

    @AutoGen(category = CATEGORY)
    @Translate.Name("Sandstorm particle density")
    @FloatSlider(min = 0.1f, max = 10.0f, step = 0.1f, format = "%.2f")
    @SerialEntry(comment = "Multiplier for how frequently sandstorm particles should appear, must be positive. Bigger values = more common.")
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
}