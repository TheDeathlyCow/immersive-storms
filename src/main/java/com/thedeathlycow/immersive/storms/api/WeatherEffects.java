package com.thedeathlycow.immersive.storms.api;

import com.thedeathlycow.immersive.storms.ISBiomeTags;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public final class WeatherEffects {
    public static Type getCurrentType(
            World world,
            BlockPos pos,
            boolean aboveSurface,
            BiPredicate<TagKey<Biome>, RegistryEntry<Biome>> tagInclusion
    ) {
        if (!world.isRaining() || (aboveSurface && world.hasRain(pos))) {
            return Type.NONE;
        } else if (aboveSurface && !world.isSkyVisible(pos)) {
            return Type.NONE;
        } else if (aboveSurface && world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return Type.NONE;
        } else {
            RegistryEntry<Biome> biome = world.getBiome(pos);
            return Type.forBiome(biome, tagInclusion);
        }
    }

    public static Type getCurrentType(
            World world,
            BlockPos pos,
            boolean aboveSurface
    ) {
        return getCurrentType(
                world,
                pos,
                aboveSurface,
                (tag, biome) -> biome.isIn(tag)
        );
    }

    public enum Type implements StringIdentifiable {
        NONE("none", null),
        SANDSTORM("sandstorm", ISBiomeTags.HAS_SANDSTORMS),
        BLIZZARD("blizzard", ISBiomeTags.HAS_BLIZZARDS),
        DENSE_FOG("dense_fog", ISBiomeTags.HAS_DENSE_FOG);

        private final String name;

        @Nullable
        private final TagKey<Biome> biomeTag;

        Type(String name, TagKey<Biome> biomeTag) {
            this.name = name;
            this.biomeTag = biomeTag;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static Type forBiome(RegistryEntry<Biome> biome, BiPredicate<TagKey<Biome>, RegistryEntry<Biome>> tagInclusion) {
            for (Type value : values()) {
                if (value.biomeTag != null && tagInclusion.test(value.biomeTag, biome)) {
                    return value;
                }
            }

            return NONE;
        }
    }

    private WeatherEffects() {

    }
}