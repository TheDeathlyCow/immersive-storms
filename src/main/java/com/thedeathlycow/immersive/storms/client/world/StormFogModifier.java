package com.thedeathlycow.immersive.storms.client.world;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfig;
import com.thedeathlycow.immersive.storms.client.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.client.config.section.GeneralSettings;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;

public final class StormFogModifier {
    // This isn't strictly necessary, as the same effect can be achieved by modifying the fog end distance environment
    // weatherAttributes with a multiplier layer. However, this implementation makes for simpler configuration through only a
    // single attribute, allows for the old fog distance config value to still be respected, and keeps compat with DH.
    public static void applyStormFogDistance(
            final FogData fog,
            final Camera camera,
            final ClientLevel level,
            final DeltaTracker deltaTracker
    ) {
        GeneralSettings config = ImmersiveStormsClient.getConfig();

        if (!config.isEnableFogChanges()) {
            return;
        }

        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
        WeatherArchetype archetype = camera.attributeProbe().getValue(ISEnvironmentAttributes.WEATHER_ARCHETYPE, partialTicks);

        if (!config.isFogTypeEnabled(archetype)) {
            return;
        }

        float stormFogDistanceMultiplier = camera.attributeProbe().getValue(ISEnvironmentAttributes.STORM_FOG_END_DISTANCE, partialTicks);
        float reduction = stormFogDistanceMultiplier * config.getFogDistanceMultiplier();
        float fogEnd = fog.environmentalEnd * reduction;

        if (reduction < 1.0f) {
            fog.environmentalEnd = fogEnd;
            fog.skyEnd *= reduction;
            fog.cloudEnd *= reduction;
        }

        if (ImmersiveStormsClient.isDistantHorizonsLoaded()) {
            setFogDistanceForDistantHorizons(reduction);
        }
    }

    private static void setFogDistanceForDistantHorizons(float reduction) {
        IDhApiConfig config = DhApi.Delayed.configs;
        if (config != null) {
            if (reduction < 0.99f) {
                config.graphics().fog().farFog().farFogMinThickness().setValue(1.0f - (reduction * 0.1f));
                config.graphics().fog().farFog().farFogMaxThickness().setValue(1.0f);
                config.graphics().fog().enableVanillaFog().setValue(true);
            } else {
                config.graphics().fog().farFog().farFogMinThickness().clearValue();
                config.graphics().fog().farFog().farFogMaxThickness().clearValue();
                config.graphics().fog().enableVanillaFog().clearValue();
            }
        }
    }

    private StormFogModifier() {

    }
}