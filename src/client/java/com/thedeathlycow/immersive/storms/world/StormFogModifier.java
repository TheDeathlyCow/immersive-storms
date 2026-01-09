package com.thedeathlycow.immersive.storms.world;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfig;
import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.mixin.client.WorldAccessor;
import com.thedeathlycow.immersive.storms.util.ISMath;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

import java.util.function.Function;

public final class StormFogModifier {

    public static int sampleWeatherFogColor(
            ClientWorld world,
            Vec3d pos,
            float tickProgress,
            int originalColor
    ) {
        final float rainGradient = world.getRainGradient(tickProgress);
        final var accumulator = new WeightedVector3fAccumulator();
        Vector3fc originalBiomeColorVector = ColorHelper.toRgbVector(originalColor);

        WeightedInterpolation.interpolate(
                pos.multiply(0.25),
                (x, y, z) -> {
                    RegistryEntry<Biome> biome = world.getBiomeForNoiseGen(x, y, z);
                    WeatherEffectType sampledType = WeatherEffectType.forBiome(biome, WeatherEffectsClient::isWeatherEffectTypeEnabled);

                    int color = sampledType.getColor();

                    if (color >= 0) {
                        return ISMath.lerp(rainGradient, originalBiomeColorVector, ISMath.unpackRgb(color));
                    } else {
                        return originalBiomeColorVector;
                    }
                },
                accumulator
        );

        return accumulator.getPackedColor();
    }

    public static void applyStartEndModifier(
            FogData data,
            Vec3d cameraPos,
            ClientWorld world,
            RenderTickCounter tickCounter
    ) {
        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();

        final float tickProgress = tickCounter.getTickProgress(false);
        final float rainGradient = world.getRainGradient(tickProgress);
        final float thunderGradient = world.getThunderGradient(tickProgress);

        var baseRadius = new Vector2f(data.environmentalStart, data.environmentalEnd);

        Vector2fc rainDistance = lerpFogDistance(cameraPos, world, baseRadius, WeatherEffectType::getRainWeatherData);

        Vector2fc thunderDistance = thunderGradient > 0
                ? lerpFogDistance(cameraPos, world, baseRadius, WeatherEffectType::getThunderWeatherData)
                : null;

        updateFogRadius(data, rainDistance, thunderDistance, rainGradient, thunderGradient, config);
    }

    public static boolean shouldApply(World world) {
        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();
        return config.isEnableFogChanges()
                && world.getRainGradient(1f) > 0f
                && ((WorldAccessor) world).invokeCanHaveWeather();
    }

    private static Vector2fc lerpFogDistance(
            Vec3d pos,
            World world,
            Vector2fc baseRadius,
            Function<WeatherEffectType, WeatherEffectType.WeatherData> fogDataSupplier
    ) {
        var samplePos = new BlockPos.Mutable();
        final var accumulator = new WeightedVector2fAccumulator();
        final int undergroundFogCutoff = world.getSeaLevel();

        // interpolate fog distances to make less jarring biome transition
        // start is stored in X and end in Y
        WeightedInterpolation.interpolate(
                pos,
                (x, y, z) -> {
                    samplePos.set(x, y, z);

                    if (y < undergroundFogCutoff) {
                        return baseRadius;
                    }

                    WeatherEffectType sampledType = WeatherEffectsClient.getCurrentType(world, samplePos, false);

                    WeatherEffectType.WeatherData fogData = fogDataSupplier.apply(sampledType);
                    if (fogData != null) {
                        return fogData.fogDistance();
                    }

                    return baseRadius;
                },
                accumulator
        );

        return accumulator.getAverageVector();
    }

    private static void updateFogRadius(
            FogData data,
            Vector2fc rainDistance,
            @Nullable Vector2fc thunderDistance,
            float rainGradient,
            float thunderGradient,
            ImmersiveStormsConfig config
    ) {
        float fogEnd = MathHelper.lerp(rainGradient, data.environmentalEnd, rainDistance.y());

        if (thunderDistance != null) {
            fogEnd = MathHelper.lerp(thunderGradient, fogEnd, thunderDistance.y());
        }

        fogEnd *= config.getFogDistanceMultiplier();

        float reduction = fogEnd / data.environmentalEnd;

        if (reduction < 1.0f) {
            data.environmentalEnd = fogEnd;
            data.skyEnd *= reduction;
            data.cloudEnd *= reduction;
        }

        if (ImmersiveStormsClient.isDistantHorizonsLoaded()) {
            setFogDistanceForDistantHorizons(reduction);
        }
    }

    private static void setFogDistanceForDistantHorizons(double reduction) {
        IDhApiConfig config = DhApi.Delayed.configs;
        if (config != null) {
            if (reduction < 0.99) {
                config.graphics().fog().farFog().farFogStartDistance().setValue(reduction * 0.1);
                config.graphics().fog().farFog().farFogEndDistance().setValue(reduction * 0.1);
                config.graphics().fog().enableVanillaFog().setValue(true);
            } else {
                config.graphics().fog().farFog().farFogStartDistance().clearValue();
                config.graphics().fog().farFog().farFogEndDistance().clearValue();
                config.graphics().fog().enableVanillaFog().clearValue();
            }
        }
    }

    private StormFogModifier() {

    }
}