package com.thedeathlycow.immersive.storms.client.mixin.client;

import com.thedeathlycow.immersive.storms.config.section.AdvancedSettings;
import com.thedeathlycow.immersive.storms.world.AttributeMapHolder;
import com.thedeathlycow.immersive.storms.world.Biome2WeatherAttributeMapView;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements AttributeMapHolder {
    @Shadow
    @Final
    private ClientPacketListener connection;

    @Override
    public void immersive_storms$rebindAttributeMap(AdvancedSettings data) {
        this.connection.immersive_storms$rebindAttributeMap(data);
    }

    @Override
    public @NonNull Biome2WeatherAttributeMapView immersive_storms$getBiomeToWeatherAttributeMap() {
        return this.connection.immersive_storms$getBiomeToWeatherAttributeMap();
    }
}