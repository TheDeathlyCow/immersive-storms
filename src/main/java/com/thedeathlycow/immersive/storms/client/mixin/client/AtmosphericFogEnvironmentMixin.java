package com.thedeathlycow.immersive.storms.client.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thedeathlycow.immersive.storms.client.world.StormFogModifier;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AtmosphericFogEnvironment.class)
public abstract class AtmosphericFogEnvironmentMixin {
    @Shadow
    private float rainFogMultiplier;

    @WrapMethod(method = "setupFog")
    private void setStormFogDistanceOverride(
            FogData fog,
            Camera camera,
            ClientLevel level,
            float renderDistance,
            DeltaTracker deltaTracker,
            Operation<Void> original
    ) {
        original.call(fog, camera, level, renderDistance, deltaTracker);

        if (this.rainFogMultiplier > 0f) {
            StormFogModifier.applyStormFogDistance(fog, camera, level, deltaTracker);
        }
    }
}

//    @WrapMethod(method = "getBaseColor")
//    protected int modifyBaseFogColor(
//            ClientLevel world,
//            Camera camera,
//            int viewDistance,
//            float skyDarkness,
//            Operation<Integer> original
//    ) {
//        int originalColor = original.call(world, camera, viewDistance, skyDarkness);
//
//        if (StormFogModifier.shouldApply(world)) {
//            return StormFogModifier.sampleWeatherFogColor(world, camera.position(), skyDarkness, originalColor);
//        } else {
//            return originalColor;
//        }
//    }
//