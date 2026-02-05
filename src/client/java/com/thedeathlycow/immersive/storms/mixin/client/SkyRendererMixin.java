package com.thedeathlycow.immersive.storms.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thedeathlycow.immersive.storms.world.StormFogModifier;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.SkyRenderState;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SkyRenderer.class)
public class SkyRendererMixin {
    @WrapMethod(
            method = "extractRenderState"
    )
    private void modifySkyColor(
            ClientLevel world,
            float tickProgress,
            Camera camera,
            SkyRenderState state,
            Operation<Void> original
    ) {
        original.call(world, tickProgress, camera, state);

        if (state.skybox == DimensionType.Skybox.OVERWORLD && StormFogModifier.shouldApply(world)) {
            state.skyColor = StormFogModifier.sampleWeatherFogColor(world, camera.position(), tickProgress, state.skyColor);
        }
    }
}