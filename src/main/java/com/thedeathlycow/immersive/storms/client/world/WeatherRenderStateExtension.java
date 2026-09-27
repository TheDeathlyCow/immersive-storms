package com.thedeathlycow.immersive.storms.client.world;

import net.minecraft.client.renderer.WeatherEffectRenderer;

public interface WeatherRenderStateExtension {
    void immersiveStorms$addBlackRainInstance(WeatherEffectRenderer.ColumnInstance instance);

    boolean immersiveStorms$isBlackRainInstance(WeatherEffectRenderer.ColumnInstance instance);
}
