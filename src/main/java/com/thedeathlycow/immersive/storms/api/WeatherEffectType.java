package com.thedeathlycow.immersive.storms.api;

import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public enum WeatherEffectType implements StringIdentifiable {
    NONE,
    SANDSTORM(
            "sandstorm",
            ISBiomeTags.HAS_SANDSTORMS,
            Vec3d.unpackRgb(0xD9AA84),
            WeatherData.LIGHT,
            WeatherData.THICK
    ),
    BLIZZARD(
            "blizzard",
            ISBiomeTags.HAS_BLIZZARDS,
            Vec3d.unpackRgb(0xFFFFFF),
            null,
            WeatherData.LIGHT
    ),
    DENSE_FOG(
            "dense_fog",
            ISBiomeTags.HAS_DENSE_FOG,
            null,
            WeatherData.THICK,
            WeatherData.THICK
    );

    private final String name;

    @Nullable
    private final TagKey<Biome> biomeTag;

    @Nullable
    private final Vec3d color;

    @Nullable
    private final WeatherEffectType.WeatherData rainFogData;

    @Nullable
    private final WeatherEffectType.WeatherData thunderFogData;

    WeatherEffectType() {
        this("none", null, null, null, null);
    }

    WeatherEffectType(
            String name,
            @Nullable TagKey<Biome> biomeTag,
            @Nullable Vec3d color,
            @Nullable WeatherEffectType.WeatherData rainFogData,
            @Nullable WeatherEffectType.WeatherData thunderFogData
    ) {
        this.name = name;
        this.biomeTag = biomeTag;
        this.color = color;
        this.rainFogData = rainFogData;
        this.thunderFogData = thunderFogData;
    }

    @Override
    public String asString() {
        return this.name;
    }

    @Nullable
    public TagKey<Biome> getBiomeTag() {
        return biomeTag;
    }

    @Nullable
    public Vec3d getColor() {
        return color;
    }

    @Nullable
    public WeatherEffectType.WeatherData getRainFogData() {
        return rainFogData;
    }

    @Nullable
    public WeatherEffectType.WeatherData getThunderFogData() {
        return thunderFogData;
    }

    @ApiStatus.Internal
    public static WeatherEffectType forBiome(RegistryEntry<Biome> biome, BiPredicate<WeatherEffectType, RegistryEntry<Biome>> allowed) {
        for (WeatherEffectType value : values()) {
            if (allowed.test(value, biome)) {
                return value;
            }
        }

        return NONE;
    }

    public static class WeatherData {
        private static final WeatherData LIGHT = new WeatherData(32.0, 64.0);
        private static final WeatherData THICK = new WeatherData(16.0, 32.0);

        private final Vec3d fogDistance;

        private WeatherData(double fogStart, double fogEnd) {
            this.fogDistance = new Vec3d(fogStart, fogEnd, 0);
        }

        public Vec3d fogDistance() {
            return fogDistance;
        }
    }
}