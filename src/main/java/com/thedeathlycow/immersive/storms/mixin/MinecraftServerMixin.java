package com.thedeathlycow.immersive.storms.mixin;

import com.thedeathlycow.immersive.storms.client.config.section.AdvancedSettings;
import com.thedeathlycow.immersive.storms.client.world.Biome2WeatherAttributeMap;
import com.thedeathlycow.immersive.storms.util.AttributeMapHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements AttributeMapHolder {
    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Unique
    private Biome2WeatherAttributeMap immersiveStorms_attributeMap;

    @Inject(
            method = "<init>", at = @At("TAIL")
    )
    private void onInit(CallbackInfo ci) {
        AdvancedSettings.HANDLER.load();
        this.immersiveStorms$rebindAttributeMap(AdvancedSettings.HANDLER.instance());
    }

    @Override
    @Unique
    @NonNull
    public Biome2WeatherAttributeMap immersive_storms$getBiomeToWeatherAttributeMap() {
        return this.immersiveStorms_attributeMap;
    }

    @Override
    @Unique
    public void immersiveStorms$rebindAttributeMap(AdvancedSettings data) {
        immersiveStorms_attributeMap = new Biome2WeatherAttributeMap();
        this.immersiveStorms_attributeMap.rebind(this.registryAccess().lookupOrThrow(Registries.BIOME), data);
    }
}