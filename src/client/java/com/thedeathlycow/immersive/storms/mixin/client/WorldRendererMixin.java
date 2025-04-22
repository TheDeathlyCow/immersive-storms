package com.thedeathlycow.immersive.storms.mixin.client;

import com.thedeathlycow.immersive.storms.util.StormEffects;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow @Nullable private ClientWorld world;

    @Inject(
            method = "renderClouds",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelCloudsInFog(
            FrameGraphBuilder frameGraphBuilder,
            CloudRenderMode mode,
            Vec3d cameraPos,
            float f,
            int color,
            float cloudHeight,
            CallbackInfo ci
    ) {
        if (this.world != null && StormEffects.shouldCancelClouds(this.world, BlockPos.ofFloored(cameraPos))) {
            ci.cancel();
        }
    }
}