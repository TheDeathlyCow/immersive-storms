package com.thedeathlycow.immersive.storms.mixin;

import com.thedeathlycow.immersive.storms.client.world.Biome2WeatherAttributeMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.WeatherAttributes;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnvironmentAttributeSystem.class)
public class EnvironmentAttributeSystemMixin {
    @Inject(
            method = "addDefaultLayers",
            at = @At("TAIL")
    )
    private static void injectWeatherLayers(EnvironmentAttributeSystem.Builder builder, Level level, CallbackInfo ci) {
        if (level.canHaveWeather()) {
            Biome2WeatherAttributeMap.addDefaultLayers(
                    builder,
                    level.registryAccess().lookupOrThrow(Registries.ENVIRONMENT_ATTRIBUTE),
                    WeatherAttributes.WeatherAccess.from(level),
                    Biome2WeatherAttributeMap.AttributeAccess.from(level),
                    level.getBiomeManager()
            );
        }
    }
}