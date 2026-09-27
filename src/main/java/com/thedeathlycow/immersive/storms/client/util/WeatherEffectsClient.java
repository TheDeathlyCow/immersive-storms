package com.thedeathlycow.immersive.storms.client.util;

import com.thedeathlycow.immersive.storms.client.config.section.BiomeConfig;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public final class WeatherEffectsClient {
    public static boolean typeAffectsBiome(WeatherEffectType type, Holder<Biome> biome) {
        return type.getBiomeTag() != null
                && !BiomeConfig.getConfig().isBiomeExcluded(biome)
                && biome.is(type.getBiomeTag()) || BiomeConfig.getConfig().isIncluded(type, biome);
    }

    private WeatherEffectsClient() {

    }
}