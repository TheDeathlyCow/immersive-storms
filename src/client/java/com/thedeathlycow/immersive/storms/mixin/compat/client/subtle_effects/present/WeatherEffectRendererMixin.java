package com.thedeathlycow.immersive.storms.mixin.compat.client.subtle_effects.present;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.world.BlackRainEffect;
import einstein.subtle_effects.init.ModRenderStateAttachmentKeys;
import einstein.subtle_effects.util.RenderStateAttachmentAccessor;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(value = WeatherEffectRenderer.class, priority = 1100)
public class WeatherEffectRendererMixin {
    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;createRainColumnInstance(Lnet/minecraft/util/RandomSource;IIIIIIF)Lnet/minecraft/client/renderer/WeatherEffectRenderer$ColumnInstance;"
            )
    )
    private WeatherEffectRenderer.ColumnInstance makeRainBlackWithSubtleEffects(
            WeatherEffectRenderer instance,
            RandomSource random,
            int ticks,
            int x,
            int bottomY,
            int topY,
            int z,
            int lightCoords,
            float partialTick,
            Operation<WeatherEffectRenderer.ColumnInstance> original,
            @Share(value = "is_black_rain", namespace = ImmersiveStorms.MOD_ID) LocalBooleanRef isBlackRain
    ) {
        WeatherEffectRenderer.ColumnInstance column = original.call(instance, random, ticks, x, bottomY, topY, z, lightCoords, partialTick);
        RenderStateAttachmentAccessor state = ((RenderStateAttachmentAccessor) (Object) column);

        Vector3f color = state.subtleEffects$get(ModRenderStateAttachmentKeys.COLOR);

        if (color != null && isBlackRain.get()) {
            state.subtleEffects$set(
                    ModRenderStateAttachmentKeys.COLOR,
                    color.mul(BlackRainEffect.COLOR_FLOAT)
            );
        }

        return column;
    }
}