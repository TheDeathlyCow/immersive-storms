package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thedeathlycow.immersive.storms.world.StormFogModifier;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.AtmosphericFogEnvironment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AtmosphericFogEnvironment.class)
public abstract class AtmosphericFogModifierMixin {
    @WrapMethod(method = "getBaseColor")
    protected int modifyBaseFogColor(
            ClientLevel world,
            Camera camera,
            int viewDistance,
            float skyDarkness,
            Operation<Integer> original
    ) {
        int originalColor = original.call(world, camera, viewDistance, skyDarkness);

        if (StormFogModifier.shouldApply(world)) {
            return StormFogModifier.sampleWeatherFogColor(world, camera.position(), skyDarkness, originalColor);
        } else {
            return originalColor;
        }
    }

    @WrapMethod(
            method = "setupFog"
    )
    private void modifyFogDistance(
            FogData data,
            Camera camera,
            ClientLevel world,
            float f,
            DeltaTracker tickCounter,
            Operation<Void> original
    ) {
        original.call(data, camera, world, f, tickCounter);

        if (StormFogModifier.shouldApply(world)) {
            StormFogModifier.applyStartEndModifier(data, camera.position(), world, tickCounter);
        }
    }
}