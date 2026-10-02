package com.thedeathlycow.immersive.storms.client.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thedeathlycow.immersive.storms.client.world.WeatherBasedAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(EnvironmentAttributeProbe.class)
public abstract class EnvironmentAttributeProbeMixin {
    @Shadow
    @Final
    private Function<EnvironmentAttribute<?>, EnvironmentAttributeProbe.ValueProbe<?>> valueProbeFactory;

    @Unique
    private WeatherBasedAttributeSystem immersiveStorms_extension;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.immersiveStorms_extension = new WeatherBasedAttributeSystem();
    }

    @Inject(method = "reset", at = @At("TAIL"))
    private void onReset(CallbackInfo ci) {
        this.immersiveStorms_extension.reset();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(final Level level, final Vec3 position, CallbackInfo ci) {
        this.immersiveStorms_extension.tick(level, position);
    }

    @WrapMethod(method = "getValue")
    private <Value> Value onGetValue(EnvironmentAttribute<Value> attribute, float partialTicks, Operation<Value> original) {
        Value baseValue = original.call(attribute, partialTicks);
        return this.immersiveStorms_extension.apply(attribute, partialTicks, baseValue);
    }
}