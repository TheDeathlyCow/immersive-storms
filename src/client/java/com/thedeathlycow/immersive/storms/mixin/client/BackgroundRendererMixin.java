package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.util.StormEffects;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Inject(
            method = "getFogColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld$Properties;getHorizonShadingRatio()F",
                    shift = At.Shift.AFTER
            )
    )
    private static void setFogColorForSandstorm(
            Camera camera,
            float tickProgress,
            ClientWorld world,
            int clampedViewDistance,
            float skyDarkness,
            CallbackInfoReturnable<Vector4f> cir,
            @Local(ordinal = 2) LocalFloatRef red,
            @Local(ordinal = 3) LocalFloatRef green,
            @Local(ordinal = 4) LocalFloatRef blue
    ) {
        Vec3d color = StormEffects.getFogColor(
                world, camera,
                red.get(), green.get(), blue.get(),
                tickProgress,
                ImmersiveStormsClient.getConfig()
        );

        if (color != null) {
            red.set((float) color.x);
            green.set((float) color.y);
            blue.set((float) color.z);
        }
    }

    @ModifyReturnValue(
            method = "applyFog",
            at = @At("TAIL")
    )
    private static Fog setFogDistanceForSandstorm(
            Fog original,
            Camera camera,
            BackgroundRenderer.FogType fogType,
            Vector4f color,
            float viewDistance,
            boolean thickenFog,
            float tickProgress,
            @Local CameraSubmersionType cameraSubmersionType,
            @Local BackgroundRenderer.FogData fogData
    ) {
        return StormEffects.updateFogDistance(original, camera, fogType, cameraSubmersionType, tickProgress, ImmersiveStormsClient.getConfig());
    }
}