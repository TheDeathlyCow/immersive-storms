package com.thedeathlycow.immersive.storms.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.thedeathlycow.immersive.storms.client.world.WeatherBasedAttributeInterpolator;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributeProbe;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnvironmentAttributeProbe.class)
public abstract class EnvironmentAttributeProbeMixin {
    @Shadow
    @Final
    private SpatialAttributeInterpolator biomeInterpolator;

    @Unique
    private WeatherBasedAttributeInterpolator immersiveStorms_extension;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.immersiveStorms_extension = new WeatherBasedAttributeInterpolator(this.biomeInterpolator);
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

    // instead of doing this in the normal tick method it is done here to save a little performance
    @WrapMethod(method = "lambda$tick$0")
    private void accumulate(double weight, Holder<Biome> biome, Operation<Void> original) {
        original.call(weight, biome);
        this.immersiveStorms_extension.sample(weight, biome);
    }

    @WrapOperation(
            method = "lambda$tick$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/attribute/SpatialAttributeInterpolator;accumulate(DLnet/minecraft/world/attribute/EnvironmentAttributeMap;)Lnet/minecraft/world/attribute/SpatialAttributeInterpolator;"
            )
    )
    private SpatialAttributeInterpolator applyModifiedMaps(
            SpatialAttributeInterpolator instance,
            double weight,
            EnvironmentAttributeMap attributes,
            Operation<SpatialAttributeInterpolator> original,
            @Local(argsOnly = true) Holder<Biome> biomeHolder
    ) {
        return original.call(instance, weight, this.immersiveStorms_extension.getClearWeatherMap(biomeHolder.value(), attributes));
    }
}