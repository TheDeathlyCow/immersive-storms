package com.thedeathlycow.immersive.storms.mixin;

import com.thedeathlycow.immersive.storms.config.section.AdvancedSettings;
import com.thedeathlycow.immersive.storms.world.AttributeMapHolder;
import com.thedeathlycow.immersive.storms.world.Biome2WeatherAttributeMapView;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements AttributeMapHolder {
    @Shadow
    public abstract MinecraftServer getServer();

    @Override
    public void immersive_storms$rebindAttributeMap(AdvancedSettings data) {
        this.getServer().immersive_storms$rebindAttributeMap(data);
    }

    @Override
    public @NonNull Biome2WeatherAttributeMapView immersive_storms$getBiomeToWeatherAttributeMap() {
        return this.getServer().immersive_storms$getBiomeToWeatherAttributeMap();
    }
}