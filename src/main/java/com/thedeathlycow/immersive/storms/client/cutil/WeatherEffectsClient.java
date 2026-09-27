package com.thedeathlycow.immersive.storms.client.cutil;

import com.thedeathlycow.immersive.storms.client.config.section.BiomeConfig;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public final class WeatherEffectsClient {
    public static boolean typeAffectsBiome(WeatherEffectType type, Holder<Biome> biome) {
        return type.getBiomeTag() != null
                && !BiomeConfig.getConfig().isBiomeExcluded(biome)
                && (ClientTags.isInWithLocalFallback(type.getBiomeTag(), biome) || BiomeConfig.getConfig().isIncluded(type, biome));
    }

    private WeatherEffectsClient() {

    }
}