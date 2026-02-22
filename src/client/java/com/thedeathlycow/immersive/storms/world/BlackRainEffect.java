package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.config.section.BiomeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class BlackRainEffect {
    public static final int COLOR = 0x20;
    public static final float COLOR_FLOAT = COLOR / 255f;

    public static boolean isBlackRain(Level level, BlockPos pos) {
        if (ImmersiveStormsClient.getConfig().isEnableBlackRain()) {
            Holder<Biome> biome = level.getBiomeManager().getNoiseBiomeAtPosition(pos);
            return BiomeConfig.getConfig().isAffectedByBlackRain(biome);
        } else {
            return false;
        }
    }

    private BlackRainEffect() {

    }
}