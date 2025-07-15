package com.thedeathlycow.immersive.storms.util;

import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public enum WeatherEffectType implements StringIdentifiable {
    NONE,
    SANDSTORM(
            "sandstorm",
            ISBiomeTags.HAS_SANDSTORMS,
            0xd96e38,
            new WeatherData(WeatherData.LIGHT_FOG, true),
            new WeatherData(WeatherData.THICK_FOG, true),
            false
    ),
    BLIZZARD(
            "blizzard",
            ISBiomeTags.HAS_BLIZZARDS,
            0xBBBBBB,
            null,
            new WeatherData(WeatherData.LIGHT_FOG, false),
            false
    ),
    DENSE_FOG(
            "dense_fog",
            ISBiomeTags.HAS_DENSE_FOG,
            -1,
            new WeatherData(WeatherData.LIGHT_FOG, false),
            new WeatherData(WeatherData.THICK_FOG, false),
            true
    );

    private final String name;

    @Nullable
    private final TagKey<Biome> biomeTag;

    private final int color;

    @Nullable
    private final WeatherEffectType.WeatherData rainWeatherData;

    @Nullable
    private final WeatherEffectType.WeatherData thunderWeatherData;

    private final boolean allowedWithRain;

    WeatherEffectType() {
        this("none", null, -1, null, null, true);
    }

    WeatherEffectType(
            String name,
            @Nullable TagKey<Biome> biomeTag,
            int color,
            @Nullable WeatherEffectType.WeatherData rainWeatherData,
            @Nullable WeatherEffectType.WeatherData thunderWeatherData,
            boolean allowedWithRain
    ) {
        this.name = name;
        this.biomeTag = biomeTag;
        this.color = color;
        this.rainWeatherData = rainWeatherData;
        this.thunderWeatherData = thunderWeatherData;
        this.allowedWithRain = allowedWithRain;
    }

    @Override
    public String asString() {
        return this.name;
    }

    @Nullable
    public TagKey<Biome> getBiomeTag() {
        return biomeTag;
    }

    public int getColor() {
        return color;
    }

    @Nullable
    public WeatherEffectType.WeatherData getWeatherData(World world) {
        if (this.thunderWeatherData != null && world.isThundering()) {
            return this.thunderWeatherData;
        } else if (world.isRaining()) {
            return this.rainWeatherData;
        } else {
            return null;
        }
    }

    @Nullable
    public WeatherEffectType.WeatherData getRainWeatherData() {
        return rainWeatherData;
    }

    @Nullable
    public WeatherEffectType.WeatherData getThunderWeatherData() {
        return thunderWeatherData;
    }

    public boolean allowedWithRain() {
        return this.allowedWithRain;
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

    public record WeatherData(
            Vec3d fogDistance,
            boolean windy
    ) {
        private static final Vec3d LIGHT_FOG = new Vec3d(32, 64, 0);
        private static final Vec3d THICK_FOG = new Vec3d(16, 32, 0);
    }
}