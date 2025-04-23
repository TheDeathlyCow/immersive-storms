package com.thedeathlycow.immersive.storms.api;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class WeatherEffectsClient {
    public static WeatherEffectType getCurrentType(
            World world,
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

    public static boolean isWeatherEffectTypeEnabled(WeatherEffectType type, RegistryEntry<Biome> biome) {
        return type.getBiomeTag() != null
                && ImmersiveStormsClient.getConfig().isEnabled(type)
                && ClientTags.isInWithLocalFallback(type.getBiomeTag(), biome);
    }

    private WeatherEffectsClient() {

    }
}