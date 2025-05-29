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
        if (!isWeatherEffected(world, pos, aboveSurface)) {
            return WeatherEffectType.NONE;
        }

        RegistryEntry<Biome> biome = world.getBiomeAccess().getBiomeForNoiseGen(pos);
        WeatherEffectType type = WeatherEffectType.forBiome(biome, tagInclusion);

        if (!type.allowedWithRain() && aboveSurface && world.hasRain(pos)) {
            return WeatherEffectType.NONE;
        }

        return type;
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

    private static boolean isWeatherEffected(
            World world,
            BlockPos pos,
            boolean aboveSurface
    ) {
        if (!world.isRaining()) {
            return false;
        } else if (aboveSurface && !world.isSkyVisible(pos)) {
            return false;
        } else if (aboveSurface && world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        } else {
            return true;
        }
    }

    private WeatherEffects() {

    }
}