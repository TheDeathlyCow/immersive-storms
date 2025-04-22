package com.thedeathlycow.immersive.storms.api;

import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
                ClientTags::isInWithLocalFallback
        );
    }

    private WeatherEffectsClient() {

    }
}