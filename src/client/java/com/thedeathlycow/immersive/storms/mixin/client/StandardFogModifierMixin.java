package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thedeathlycow.immersive.storms.world.StormFogModifier;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.AtmosphericFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.StandardFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StandardFogModifier.class)
public abstract class StandardFogModifierMixin {
    @Unique
    protected final StormFogModifier immersiveStormsFogModifier = new StormFogModifier();

    @WrapMethod(
            method = "getFogColor"
    )
    protected int overrideGetFogColor(
            ClientWorld world,
            Camera camera,
            int viewDistance,
            float skyDarkness,
            Operation<Integer> original
    ) {
        return original.call(world, camera, viewDistance, skyDarkness);
    }

    @Mixin(AtmosphericFogModifier.class)
    private static class AtmosphericFogModifierMixin extends StandardFogModifierMixin {
        @Override
        protected int overrideGetFogColor(
                ClientWorld world,
                Camera camera,
                int viewDistance,
                float skyDarkness,
                Operation<Integer> original
        ) {
            int modifiedColor = this.immersiveStormsFogModifier.getFogColor(world, camera, viewDistance, skyDarkness);
            return modifiedColor < 0 ?
                    super.overrideGetFogColor(world, camera, viewDistance, skyDarkness, original)
                    : modifiedColor;
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

            if (this.immersiveStormsFogModifier.shouldApply(null, cameraEntity)) {
                this.immersiveStormsFogModifier.applyStartEndModifier(data, cameraEntity, cameraPos, world, viewDistance, tickCounter);
            }
        }
    }
}