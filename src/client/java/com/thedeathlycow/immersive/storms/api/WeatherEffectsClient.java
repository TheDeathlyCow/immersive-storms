package com.thedeathlycow.immersive.storms.api;

import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.function.BiPredicate;

public final class WeatherEffectsClient {
    public static WeatherEffects.Type getCurrentType(
            World world,
            BlockPos pos,
            boolean aboveSurface,
            BiPredicate<TagKey<Biome>, RegistryEntry<Biome>> tagInclusion
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