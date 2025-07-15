package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.thedeathlycow.immersive.storms.world.StormFogModifier;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.AtmosphericFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.StandardFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StandardFogModifier.class)
public abstract class StandardFogModifierMixin {
    @Unique
    protected final StormFogModifier immersiveStormsFogModifier = new StormFogModifier();

    @WrapOperation(
            method = "getFogColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"
            )
    )
    protected Vec3d modifyBaseFogColor(
            Vec3d pos,
            CubicSampler.RgbFetcher rgbFetcher,
            Operation<Vec3d> original,
            @Local(argsOnly = true) ClientWorld world
    ) {
        return original.call(pos, rgbFetcher);
    }

    @Mixin(AtmosphericFogModifier.class)
    private static class AtmosphericFogModifierMixin extends StandardFogModifierMixin {
        @Override
        protected Vec3d modifyBaseFogColor(Vec3d pos, CubicSampler.RgbFetcher rgbFetcher, Operation<Vec3d> original, ClientWorld world) {
            if (this.immersiveStormsFogModifier.shouldApply(world)) {
                return this.immersiveStormsFogModifier.sampleWeatherFogColor(world, pos);
            } else {
                return super.modifyBaseFogColor(pos, rgbFetcher, original, world);
            }
        }

        @WrapMethod(
                method = "applyStartEndModifier"
        )
        private void modifyFogDistance(
                FogData data,
                Entity cameraEntity,
                BlockPos cameraPos,
                ClientWorld world,
                float viewDistance,
                RenderTickCounter tickCounter,
                Operation<Void> original
        ) {
            original.call(data, cameraEntity, cameraPos, world, viewDistance, tickCounter);

            if (this.immersiveStormsFogModifier.shouldApply(world)) {
                this.immersiveStormsFogModifier.applyStartEndModifier(data, cameraEntity.getEyePos(), world, tickCounter);
            }
        }
    }
}