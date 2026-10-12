package com.thedeathlycow.immersive.storms.client.world;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum WeatherArchetype implements StringRepresentable {
    NORMAL("normal"),
    SANDSTORM("sandstorm"),
    BLIZZARD("blizzard"),
    SPOOKY("spooky");

    public static final Codec<WeatherArchetype> CODEC = StringRepresentable.fromValues(WeatherArchetype::values);

    private final String name;

    WeatherArchetype(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
