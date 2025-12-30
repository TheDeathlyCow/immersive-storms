package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.thedeathlycow.immersive.storms.world.StormFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
//    @WrapOperation(
//            method = "getSkyColor",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"
//            )
//    )
//    private Vec3d modifySkyColor(Vec3d pos, CubicSampler.RgbFetcher rgbFetcher, Operation<Vec3d> original) {
//        ClientWorld world = (ClientWorld) (Object) this;
//        if (StormFogModifier.shouldApply(world)) {
//            return StormFogModifier.sampleWeatherFogColor(world, pos, Biome::getSkyColor);
//        }
//
//        return original.call(pos, rgbFetcher);
//    }
}