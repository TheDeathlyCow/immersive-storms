package com.thedeathlycow.immersive.storms.api;

import com.thedeathlycow.immersive.storms.ISBiomeTags;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public enum WeatherEffectType implements StringIdentifiable {
    NONE("none", null, null),
    SANDSTORM("sandstorm", ISBiomeTags.HAS_SANDSTORMS, Vec3d.unpackRgb(0xD9AA84)),
    BLIZZARD("blizzard", ISBiomeTags.HAS_BLIZZARDS, Vec3d.unpackRgb(0xFFFFFF)),
    DENSE_FOG("dense_fog", ISBiomeTags.HAS_DENSE_FOG, Vec3d.unpackRgb(0x999999));

    private final String name;

    @Nullable
    private final TagKey<Biome> biomeTag;

    @Nullable
    private final Vec3d color;

    WeatherEffectType(String name, TagKey<Biome> biomeTag, Vec3d color) {
        this.name = name;
        this.biomeTag = biomeTag;
        this.color = color;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Vec3d getColor() {
        return color;
    }

    @ApiStatus.Internal
    public static WeatherEffectType forBiome(RegistryEntry<Biome> biome, BiPredicate<TagKey<Biome>, RegistryEntry<Biome>> tagInclusion) {
        for (WeatherEffectType value : values()) {
            if (value.biomeTag != null && tagInclusion.test(value.biomeTag, biome)) {
                return value;
            }
        }

        return NONE;
    }
}