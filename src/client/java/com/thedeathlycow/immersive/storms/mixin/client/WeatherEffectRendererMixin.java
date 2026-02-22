package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedeathlycow.immersive.storms.world.WeatherRenderStateExtension;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.WeatherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {
    private final ThreadLocal<WeatherRenderState> sharedRenderState = new ThreadLocal<>();

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;getPrecipitationAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"
            )
    )
    private Biome.Precipitation checkPaleRainBiome(
            WeatherEffectRenderer instance,
            Level level,
            BlockPos pos,
            Operation<Biome.Precipitation> original,
            @Share("is_pale_rain") LocalBooleanRef isPaleRain
    ) {
        Biome.Precipitation precipitation = original.call(instance, level, pos);

        if (precipitation == Biome.Precipitation.RAIN) {
            Holder<Biome> biome = level.getBiomeManager().getNoiseBiomeAtPosition(pos);

            // TODO: replace with tag/config driven approach
            isPaleRain.set(biome.is(Biomes.PALE_GARDEN));
        } else {
            isPaleRain.set(false);
        }

        return precipitation;
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;createRainColumnInstance(Lnet/minecraft/util/RandomSource;IIIIIIF)Lnet/minecraft/client/renderer/WeatherEffectRenderer$ColumnInstance;"
            )
    )
    private WeatherEffectRenderer.ColumnInstance extractPaleRainInstance(
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
            @Share("is_pale_rain") LocalBooleanRef isPaleRain,
            @Local(argsOnly = true) WeatherRenderState weatherRenderState
    ) {
        WeatherEffectRenderer.ColumnInstance columnInstance = original.call(instance, random, ticks, x, bottomY, topY, z, lightCoords, partialTick);

        if (isPaleRain.get()) {
            ((WeatherRenderStateExtension) weatherRenderState).immersiveStorms$addPaleRainInstance(columnInstance);
        }

        return columnInstance;
    }

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void captureWeatherRenderState(
            MultiBufferSource bufferSource,
            Vec3 cameraPosition,
            WeatherRenderState renderState,
            CallbackInfo ci
    ) {
        this.sharedRenderState.set(renderState);
    }

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void clearWeatherRenderState(
            MultiBufferSource bufferSource,
            Vec3 cameraPosition,
            WeatherRenderState renderState,
            CallbackInfo ci
    ) {
        this.sharedRenderState.remove();
    }

    @WrapOperation(
            method = "renderInstances",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer setPaleRainColor(
            VertexConsumer instance,
            int color,
            Operation<VertexConsumer> original,
            @Local WeatherEffectRenderer.ColumnInstance columnInstance
    ) {
        WeatherRenderState renderState = this.sharedRenderState.get();

        if (renderState != null) {
            boolean isPaleRain = ((WeatherRenderStateExtension) renderState).immersiveStorms$isPaleRainInstance(columnInstance);

            if (isPaleRain) {
                color = ARGB.color(0, 0, 0);
            }
        }

        return original.call(instance, color);
    }
}