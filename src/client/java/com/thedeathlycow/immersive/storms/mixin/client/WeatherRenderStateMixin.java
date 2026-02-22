package com.thedeathlycow.immersive.storms.mixin.client;

import com.thedeathlycow.immersive.storms.world.WeatherRenderStateExtension;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.WeatherRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(WeatherRenderState.class)
public class WeatherRenderStateMixin implements WeatherRenderStateExtension {
    @Unique
    private final Set<WeatherEffectRenderer.ColumnInstance> immersiveStorms$blackRainInstances = new HashSet<>();

    @Override
    @Unique
    public void immersiveStorms$addBlackRainInstance(WeatherEffectRenderer.ColumnInstance instance) {
        this.immersiveStorms$blackRainInstances.add(instance);
    }

    @Override
    public boolean immersiveStorms$isBlackRainInstance(WeatherEffectRenderer.ColumnInstance instance) {
        return this.immersiveStorms$blackRainInstances.contains(instance);
    }

    @Inject(
            method = "reset",
            at = @At("TAIL")
    )
    private void onReset(CallbackInfo ci) {
        this.immersiveStorms$blackRainInstances.clear();
    }
}