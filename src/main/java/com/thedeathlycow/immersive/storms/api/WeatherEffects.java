package com.thedeathlycow.immersive.storms.api;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.function.BiPredicate;

public final class WeatherEffects {
    public static WeatherEffectType getCurrentType(
            World world,
            BlockPos pos,
            boolean aboveSurface,
            BiPredicate<TagKey<Biome>, RegistryEntry<Biome>> tagInclusion
    ) {
        if (!world.isRaining() || (aboveSurface && world.hasRain(pos))) {
            return WeatherEffectType.NONE;
        } else if (aboveSurface && !world.isSkyVisible(pos)) {
            return WeatherEffectType.NONE;
        } else if (aboveSurface && world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return WeatherEffectType.NONE;
        } else {
            RegistryEntry<Biome> biome = world.getBiome(pos);
            return WeatherEffectType.forBiome(biome, tagInclusion);
        }
    }

    public static WeatherEffectType getCurrentType(
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

    private WeatherEffects() {

    }
}