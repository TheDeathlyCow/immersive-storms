package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import com.thedeathlycow.immersive.storms.world.BlackRainEffect;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @WrapOperation(
            method = "tickWeatherEffects",
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
            @Local(name = "rainParticlePosition") BlockPos pos
    ) {
        if (particle == ParticleTypes.RAIN && BlackRainEffect.isBlackRain(instance, pos)) {
            particle = ISParticleTypes.BLACK_RAIN;
        }

        original.call(instance, particle, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}