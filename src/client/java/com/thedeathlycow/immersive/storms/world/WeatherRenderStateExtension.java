package com.thedeathlycow.immersive.storms.world;

import net.minecraft.client.renderer.WeatherEffectRenderer;

public interface WeatherRenderStateExtension {
    void immersiveStorms$addPaleRainInstance(WeatherEffectRenderer.ColumnInstance instance);

    boolean immersiveStorms$isPaleRainInstance(WeatherEffectRenderer.ColumnInstance instance);
}
