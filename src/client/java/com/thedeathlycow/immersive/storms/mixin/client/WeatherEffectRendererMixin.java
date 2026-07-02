package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import com.thedeathlycow.immersive.storms.world.BlackRainEffect;
import com.thedeathlycow.immersive.storms.world.WeatherRenderStateExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {
    private final ThreadLocal<WeatherRenderState> sharedRenderState = new ThreadLocal<>();

    //
    // Render state extraction
    //

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"
            )
    )
    private Biome.Precipitation checkBlackRainBiome(
            ClientLevel instance,
            BlockPos pos,
            Operation<Biome.Precipitation> original,
            @Share(value = "is_black_rain", namespace = ImmersiveStorms.MOD_ID) LocalBooleanRef isBlackRain
    ) {
        Biome.Precipitation precipitation = original.call(instance, pos);

        if (precipitation == Biome.Precipitation.RAIN) {
            isBlackRain.set(BlackRainEffect.isBlackRain(instance, pos));
        } else {
            isBlackRain.set(false);
        }

        return precipitation;
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;createRainColumnInstance(Lnet/minecraft/util/RandomSource;JIIIIIF)Lnet/minecraft/client/renderer/WeatherEffectRenderer$ColumnInstance;"
            )
    )
    private WeatherEffectRenderer.ColumnInstance extractBlackRainInstance(
            WeatherEffectRenderer instance,
            RandomSource random,
            long ticks,
            int x,
            int bottomY,
            int topY,
            int z,
            int lightCoords,
            float partialTicks,
            Operation<WeatherEffectRenderer.ColumnInstance> original,
            @Share(value = "is_black_rain", namespace = ImmersiveStorms.MOD_ID) LocalBooleanRef isBlackRain,
            @Local(argsOnly = true) WeatherRenderState weatherRenderState
    ) {
        WeatherEffectRenderer.ColumnInstance columnInstance = original.call(instance, random, ticks, x, bottomY, topY, z, lightCoords, partialTicks);

        if (isBlackRain.get()) {
            ((WeatherRenderStateExtension) weatherRenderState).immersiveStorms$addBlackRainInstance(columnInstance);
        }

        return columnInstance;
    }

    //
    // Set rain column colour
    //

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void captureWeatherRenderState(Vec3 cameraPos, WeatherRenderState renderState, CallbackInfo ci) {
        this.sharedRenderState.set(renderState);
    }

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void clearWeatherRenderState(Vec3 cameraPos, WeatherRenderState renderState, CallbackInfo ci) {
        this.sharedRenderState.remove();
    }

    @WrapOperation(
            method = "renderInstances",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer setBlackRainColumnColor(
            VertexConsumer instance,
            int color,
            Operation<VertexConsumer> original,
            @Local(name = "column") WeatherEffectRenderer.ColumnInstance columnInstance
    ) {
        WeatherRenderState renderState = this.sharedRenderState.get();

        if (renderState != null) {
            boolean isBlackRain = ((WeatherRenderStateExtension) renderState).immersiveStorms$isBlackRainInstance(columnInstance);

            if (isBlackRain) {
                color = ARGB.color(ARGB.alpha(color), BlackRainEffect.COLOR, BlackRainEffect.COLOR, BlackRainEffect.COLOR);
            }
        }

        return original.call(instance, color);
    }
}