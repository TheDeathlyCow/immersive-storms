package com.thedeathlycow.immersive.storms.util;

import java.util.function.BiPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public final class WeatherEffects {
    public static WeatherEffectType getCurrentType(
            Level world,
            BlockPos pos,
            boolean aboveSurface,
            BiPredicate<WeatherEffectType, Holder<Biome>> tagInclusion
    ) {
        if (!isWeatherAffected(world, pos, aboveSurface)) {
            return WeatherEffectType.NONE;
        }

        Holder<Biome> biome = world.getBiomeManager().getNoiseBiomeAtPosition(pos);
        WeatherEffectType type = WeatherEffectType.forBiome(biome, tagInclusion);

        if (!type.allowedWithRain() && aboveSurface && world.isRainingAt(pos)) {
            return WeatherEffectType.NONE;
        }

        return type;
    }

    public static WeatherEffectType getCurrentType(
            Level world,
            BlockPos pos,
            boolean aboveSurface
    ) {
        return getCurrentType(
                world,
                pos,
                aboveSurface,
                (type, biome) -> type.getBiomeTag() != null && biome.is(type.getBiomeTag())
        );
    }

    private static boolean isWeatherAffected(
            Level world,
            BlockPos pos,
            boolean aboveSurface
    ) {
        if (!world.isRaining()) {
            return false;
        } else if (aboveSurface && !world.canSeeSky(pos)) {
            return false;
        } else if (aboveSurface && world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        } else {
            return true;
        }
    }

    private WeatherEffects() {

    }
}