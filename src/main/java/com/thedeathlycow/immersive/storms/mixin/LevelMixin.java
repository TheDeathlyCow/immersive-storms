package com.thedeathlycow.immersive.storms.mixin;

import com.thedeathlycow.immersive.storms.client.config.section.AttributeConfigData;
import com.thedeathlycow.immersive.storms.client.world.Biome2WeatherAttributeMap;
import com.thedeathlycow.immersive.storms.client.world.LevelExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor, LevelExtension {
    @Unique
    private Biome2WeatherAttributeMap immersiveStorms_attributeMap = new Biome2WeatherAttributeMap();

    @Inject(
            method = "<init>", at = @At("TAIL")
    )
    private void onInit(CallbackInfo ci) {
        this.immersiveStorms_attributeMap.rebind(this.registryAccess().lookupOrThrow(Registries.BIOME), AttributeConfigData.HANDLER.instance());
    }

    @Override
    @Unique
    @NonNull
    public Biome2WeatherAttributeMap immersive_storms$getBiomeToWeatherAttributeMap() {
        return this.immersiveStorms_attributeMap;
    }

    @Override
    @Unique
    public void immersiveStorms$rebindAttributeMap(AttributeConfigData data) {
        this.immersiveStorms_attributeMap.rebind(this.registryAccess().lookupOrThrow(Registries.BIOME), data);
    }
}