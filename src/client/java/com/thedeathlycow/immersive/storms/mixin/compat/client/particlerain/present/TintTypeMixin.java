package com.thedeathlycow.immersive.storms.mixin.compat.client.particlerain.present;

import com.thedeathlycow.immersive.storms.world.BlackRainEffect;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pigcart.particlerain.config.ParticleData;

@Mixin(targets = "pigcart/particlerain/config/ParticleData$TintType$1")
public class TintTypeMixin {
    @Inject(
            method = "applyTint", // minecraft dev plugin says this doesnt work but it does lol
            at = @At("TAIL")
    )
    private void blackenParticleRain(SingleQuadParticle p, ClientLevel level, BlockPos pos, ParticleData opts, CallbackInfo ci) {
        if (BlackRainEffect.isBlackRain(level, pos)) {
            p.setColor(BlackRainEffect.COLOR_FLOAT, BlackRainEffect.COLOR_FLOAT, BlackRainEffect.COLOR_FLOAT);
        }
    }
}