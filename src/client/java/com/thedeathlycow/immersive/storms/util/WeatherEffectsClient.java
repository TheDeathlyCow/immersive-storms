package com.thedeathlycow.immersive.storms.util;

import WeatherEffectType;
import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class WeatherEffectsClient {
    public static WeatherEffectType getCurrentType(
            Level world,
            BlockPos pos,
            boolean aboveSurface
    ) {
        return WeatherEffects.getCurrentType(
                world,
                pos,
                aboveSurface,
                WeatherEffectsClient::isWeatherEffectTypeEnabled
        );
    }

    public static boolean isWeatherEffectTypeEnabled(WeatherEffectType type, Holder<Biome> biome) {
        return type.getBiomeTag() != null
                && ImmersiveStormsClient.getConfig().isEnabled(type)
                && ClientTags.isInWithLocalFallback(type.getBiomeTag(), biome);
    }

    private WeatherEffectsClient() {

    }
}