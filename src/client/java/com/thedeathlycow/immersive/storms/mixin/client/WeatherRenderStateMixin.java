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
    private final Set<WeatherEffectRenderer.ColumnInstance> immersiveStorms$paleRainInstances = new HashSet<>();

    @Override
    @Unique
    public void immersiveStorms$addPaleRainInstance(WeatherEffectRenderer.ColumnInstance instance) {
        this.immersiveStorms$paleRainInstances.add(instance);
    }

    @Override
    public boolean immersiveStorms$isPaleRainInstance(WeatherEffectRenderer.ColumnInstance instance) {
        return this.immersiveStorms$paleRainInstances.contains(instance);
    }

    @Inject(
            method = "reset",
            at = @At("TAIL")
    )
    private void onReset(CallbackInfo ci) {
        this.immersiveStorms$paleRainInstances.clear();
    }
}