package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.client.config.section.AttributeConfigData;
import com.thedeathlycow.immersive.storms.client.config.section.BiomeConfig;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class BlackRainEffect {
    public static final int COLOR = 0x20;
    public static final float COLOR_FLOAT = COLOR / 255f;

    public static boolean isBlackRain(Level level, BlockPos pos) {
        if (ImmersiveStormsClient.getConfig().isEnableBlackRain()) {
            LevelExtension ext = (LevelExtension) level;
            Holder<Biome> biome = level.getBiomeManager().getNoiseBiomeAtPosition(pos);
            ResourceKey<Biome> biomeKey = biome.unwrapKey().orElseThrow();

            AttributeConfigData.WeatherAttributeData weatherAttributes = ext.immersive_storms$getBiomeToWeatherAttributeMap().getWeatherAttributes(biomeKey);

            return level.environmentAttributes().getValue(ISEnvironmentAttributes.HAS_BLACK_RAIN, pos)
                    || weatherAttributes.rainAttributes().applyModifier(ISEnvironmentAttributes.HAS_BLACK_RAIN, false)
                    || weatherAttributes.thunderAttributes().applyModifier(ISEnvironmentAttributes.HAS_BLACK_RAIN, false);
        } else {
            return false;
        }
    }

    private BlackRainEffect() {

    }
}