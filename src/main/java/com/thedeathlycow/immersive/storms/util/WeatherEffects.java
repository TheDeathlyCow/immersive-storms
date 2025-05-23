package com.thedeathlycow.immersive.storms.util;

import net.minecraft.registry.entry.RegistryEntry;
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
            BiPredicate<WeatherEffectType, RegistryEntry<Biome>> tagInclusion
    ) {
        if (!world.isRaining() || (aboveSurface && world.hasRain(pos))) {
            return WeatherEffectType.NONE;
        } else if (aboveSurface && !world.isSkyVisible(pos)) {
            return WeatherEffectType.NONE;
        } else if (aboveSurface && world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return WeatherEffectType.NONE;
        } else {
            RegistryEntry<Biome> biome = world.getBiomeAccess().getBiomeForNoiseGen(pos);
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
                (type, biome) -> type.getBiomeTag() != null && biome.isIn(type.getBiomeTag())
        );
    }

    private WeatherEffects() {

    }
}