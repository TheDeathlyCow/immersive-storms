package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import com.thedeathlycow.immersive.storms.world.BlackRainEffect;
import com.thedeathlycow.immersive.storms.world.WeatherRenderStateExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.WeatherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
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
                    target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;getPrecipitationAt(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"
            )
    )
    private Biome.Precipitation checkBlackRainBiome(
            WeatherEffectRenderer instance,
            Level level,
            BlockPos pos,
            Operation<Biome.Precipitation> original,
            @Share("is_black_rain") LocalBooleanRef isBlackRain
    ) {
        Biome.Precipitation precipitation = original.call(instance, level, pos);

        if (precipitation == Biome.Precipitation.RAIN) {
            isBlackRain.set(BlackRainEffect.isBlackRain(level, pos));
        } else {
            isBlackRain.set(false);
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
    private WeatherEffectRenderer.ColumnInstance extractBlackRainInstance(
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
            @Share("is_black_rain") LocalBooleanRef isBlackRain,
            @Local(argsOnly = true) WeatherRenderState weatherRenderState
    ) {
        WeatherEffectRenderer.ColumnInstance columnInstance = original.call(instance, random, ticks, x, bottomY, topY, z, lightCoords, partialTick);

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
    private VertexConsumer setBlackRainColumnColor(
            VertexConsumer instance,
            int color,
            Operation<VertexConsumer> original,
            @Local WeatherEffectRenderer.ColumnInstance columnInstance
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

    //
    // Particle colouring
    //

    @WrapOperation(
            method = "tickRainParticles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
            )
    )
    private void setBlackRainParticleColor(
            ClientLevel instance,
            ParticleOptions particle,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed,
            Operation<Void> original,
            @Local(ordinal = 2) BlockPos pos
    ) {
        if (BlackRainEffect.isBlackRain(instance, pos)) {
            particle = ISParticleTypes.BLACK_RAIN;
        }

        original.call(instance, particle, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}