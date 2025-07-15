package com.thedeathlycow.immersive.storms.mixin.client;

import com.thedeathlycow.immersive.storms.world.StormFogModifier;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Shadow @Final private static List<FogModifier> FOG_MODIFIERS;

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void appendStormFogModifier(CallbackInfo ci) {
        FOG_MODIFIERS.add(FOG_MODIFIERS.size() - 1, new StormFogModifier());
    }
}