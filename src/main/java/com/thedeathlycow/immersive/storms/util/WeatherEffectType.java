package com.thedeathlycow.immersive.storms.util;

import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.function.BiPredicate;

public enum WeatherEffectType implements StringRepresentable {
    NONE,
    SANDSTORM(
            "sandstorm",
            ISBiomeTags.HAS_SANDSTORMS,
            new WeatherData(WeatherData.LIGHT_FOG, true, 0xD96E38),
            new WeatherData(WeatherData.THICK_FOG, true, 0xD96E38),
            Biome.Precipitation.NONE
    ),
    BLIZZARD(
            "blizzard",
            ISBiomeTags.HAS_BLIZZARDS,
            null,
            new WeatherData(WeatherData.LIGHT_FOG, true, 0x77797A),
            Biome.Precipitation.SNOW
    ),
    DENSE_FOG(
            "dense_fog",
            ISBiomeTags.HAS_DENSE_FOG,
            new WeatherData(WeatherData.LIGHT_FOG, false, -1),
            new WeatherData(WeatherData.THICK_FOG, false, -1),
            null
    );

    private final String name;

    @Nullable
    private final TagKey<Biome> biomeTag;

    @Nullable
    private final WeatherEffectType.WeatherData rainWeatherData;

    @Nullable
    private final WeatherEffectType.WeatherData thunderWeatherData;

    @Nullable
    private final Biome.Precipitation requiredPrecipitation;

    WeatherEffectType() {
        this("none", null, null, null, null);
    }

    WeatherEffectType(
            String name,
            @Nullable TagKey<Biome> biomeTag,
            @Nullable WeatherEffectType.WeatherData rainWeatherData,
            @Nullable WeatherEffectType.WeatherData thunderWeatherData,
            Biome.Precipitation requiredPrecipitation
    ) {
        this.name = name;
        this.biomeTag = biomeTag;
        this.rainWeatherData = rainWeatherData;
        this.thunderWeatherData = thunderWeatherData;
        this.requiredPrecipitation = requiredPrecipitation;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Nullable
    public TagKey<Biome> getBiomeTag() {
        return biomeTag;
    }

    public int getFogColor(Level level) {
        WeatherEffectType.WeatherData data = this.getWeatherData(level);
        return data != null ? data.fogColor() : -1;
    }

    @Nullable
    public WeatherEffectType.WeatherData getWeatherData(Level world) {
        if (this.thunderWeatherData != null && world.getThunderLevel(1f) > 0f) {
            return this.thunderWeatherData;
        } else if (world.getRainLevel(1f) > 0f) {
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

    @Nullable
    public Biome.Precipitation requiredPrecipitation() {
        return this.requiredPrecipitation;
    }

    @ApiStatus.Internal
    public static WeatherEffectType forBiome(Holder<Biome> biome, BiPredicate<WeatherEffectType, Holder<Biome>> allowed) {
        for (WeatherEffectType value : values()) {
            if (allowed.test(value, biome)) {
                return value;
            }
        }

        return NONE;
    }

    public record WeatherData(
            Vector2fc fogDistance,
            boolean windy,
            int fogColor
    ) {
        private static final Vector2fc LIGHT_FOG = new Vector2f(32, 64);
        private static final Vector2fc THICK_FOG = new Vector2f(16, 32);
    }
}