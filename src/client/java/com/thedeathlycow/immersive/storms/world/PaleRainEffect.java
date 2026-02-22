package com.thedeathlycow.immersive.storms.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public final class PaleRainEffect {
    public static final int COLOR = 0x20;
    public static final float COLOR_FLOAT = COLOR / 255f;

    public static boolean isPaleRain(Level level, BlockPos pos) {
        Holder<Biome> biome = level.getBiomeManager().getNoiseBiomeAtPosition(pos);

        // TODO: replace with tag/config driven approach
        return biome.is(Biomes.PALE_GARDEN);
    }

    private PaleRainEffect() {

    }
}