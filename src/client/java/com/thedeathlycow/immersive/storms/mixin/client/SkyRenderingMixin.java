package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thedeathlycow.immersive.storms.world.StormFogModifier;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.render.state.SkyRenderState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SkyRendering.class)
public class SkyRenderingMixin {
    @WrapMethod(
            method = "updateRenderState"
    )
    private void modifySkyColor(
            ClientWorld world,
            float tickProgress,
            Camera camera,
            SkyRenderState state,
            Operation<Void> original
    ) {
        original.call(world, tickProgress, camera, state);

        if (state.skybox == DimensionType.Skybox.OVERWORLD && StormFogModifier.shouldApply(world)) {
            state.skyColor = StormFogModifier.sampleWeatherFogColor(world, camera.getCameraPos(), tickProgress, state.skyColor);
        }
    }
}