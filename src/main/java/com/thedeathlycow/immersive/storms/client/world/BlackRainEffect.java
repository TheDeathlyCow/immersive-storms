package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class BlackRainEffect {
    public static final int COLOR = 0x20;
    public static final float COLOR_FLOAT = COLOR / 255f;

    public static boolean isBlackRain(Level level, BlockPos pos) {
        if (ImmersiveStormsClient.getConfig().isEnableBlackRain()) {
            return level.environmentAttributes().getValue(ISEnvironmentAttributes.HAS_BLACK_RAIN, pos);
        } else {
            return false;
        }
    }

    private BlackRainEffect() {

    }
}