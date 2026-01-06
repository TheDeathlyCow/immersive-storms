package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
import com.thedeathlycow.immersive.storms.world.StormFogModifier;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.AtmosphericFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AtmosphericFogModifier.class)
public abstract class AtmosphericFogModifierMixin {
    @WrapMethod(method = "getFogColor")
    protected int modifyBaseFogColor(
            ClientWorld world,
            Camera camera,
            int viewDistance,
            float skyDarkness,
            Operation<Integer> original
    ) {
        int originalColor = original.call(world, camera, viewDistance, skyDarkness);

        if (StormFogModifier.shouldApply(world)) {
            return StormFogModifier.sampleWeatherFogColor(world, camera.getCameraPos(), skyDarkness, originalColor);
        } else {
            return originalColor;
        }
    }

//    @WrapMethod(
//            method = "applyStartEndModifier"
//    )
//    private void modifyFogDistance(
//            FogData data,
//            Camera camera,
//            ClientWorld world,
//            float f,
//            RenderTickCounter tickCounter,
//            Operation<Void> original
//    ) {
//        original.call(data, camera, world, f, tickCounter);
//
//        if (StormFogModifier.shouldApply(world)) {
//            StormFogModifier.applyStartEndModifier(data, camera.getEyePos(), world, tickCounter);
//        }
//    }
}