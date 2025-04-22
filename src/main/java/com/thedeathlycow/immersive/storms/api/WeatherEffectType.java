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
    NONE,
    SANDSTORM(
            "sandstorm",
            ISBiomeTags.HAS_SANDSTORMS,
            Vec3d.unpackRgb(0xD9AA84),
            WeatherFogData.LIGHT,
            WeatherFogData.THICK
    ),
    BLIZZARD(
            "blizzard",
            ISBiomeTags.HAS_BLIZZARDS,
            Vec3d.unpackRgb(0xFFFFFF),
            null,
            WeatherFogData.LIGHT
    ),
    DENSE_FOG(
            "dense_fog",
            ISBiomeTags.HAS_DENSE_FOG,
            null,
            WeatherFogData.THICK,
            WeatherFogData.THICK
    );

    private final String name;

    @Nullable
    private final TagKey<Biome> biomeTag;

    @Nullable
    private final Vec3d color;

    @Nullable
    private final WeatherFogData rainFogData;

    @Nullable
    private final WeatherFogData thunderFogData;

    WeatherEffectType() {
        this("none", null, null, null, null);
    }

    WeatherEffectType(
            String name,
            @Nullable TagKey<Biome> biomeTag,
            @Nullable Vec3d color,
            @Nullable WeatherFogData rainFogData,
            @Nullable WeatherFogData thunderFogData
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
    public Vec3d getColor() {
        return color;
    }

    @Nullable
    public WeatherFogData getRainFogData() {
        return rainFogData;
    }

    @Nullable
    public WeatherFogData getThunderFogData() {
        return thunderFogData;
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

    public record WeatherFogData(
            Vec3d fogDistance
    ) {
        public static final WeatherFogData LIGHT = new WeatherFogData(32.0, 64.0);
        public static final WeatherFogData THICK = new WeatherFogData(16.0, 32.0);

        public WeatherFogData(double fogStart, double fogEnd) {
            this(new Vec3d(fogStart, fogEnd, 0));
        }
    }
}