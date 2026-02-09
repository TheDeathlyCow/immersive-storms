package com.thedeathlycow.immersive.storms.world;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.interfaces.config.IDhApiConfig;
import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.mixin.client.LevelAccessor;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffects;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.GaussianSampler;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

import java.util.function.Function;

public final class StormFogModifier {
    public static int sampleWeatherFogColor(
            ClientLevel level,
            Vec3 pos,
            float tickProgress,
            int originalColor
    ) {
        final float rainLevel = level.getRainLevel(tickProgress);
        final float thunderLevel = level.getThunderLevel(tickProgress);

        final var accumulator = new WeightedVector3fAccumulator();
        Vector3fc originalBiomeColorVector = ARGB.vector3fFromRGB24(originalColor);

        GaussianSampler.sample(
                pos.scale(0.25),
                (x, y, z) -> {
                    Holder<Biome> biome = level.getNoiseBiome(x, y, z);
                    WeatherEffectType sampledType = WeatherEffectType.forBiome(
                            biome,
                            (type, biomeHolder) -> {
                                return ImmersiveStormsClient.getConfig().isEnabled(type)
                                        && WeatherEffectsClient.typeAffectsBiome(type, biomeHolder);
                            }
                    );

                    return sampledType.getFogColor(originalBiomeColorVector, rainLevel, thunderLevel);
                },
                accumulator
        );

        return accumulator.getPackedColor();
    }

    public static void applyStartEndModifier(
            FogData data,
            Vec3 cameraPos,
            ClientLevel world,
            DeltaTracker tickCounter
    ) {
        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();

        final float tickProgress = tickCounter.getGameTimeDeltaPartialTick(false);
        final float rainGradient = world.getRainLevel(tickProgress);
        final float thunderGradient = world.getThunderLevel(tickProgress);

        var baseRadius = new Vector2f(data.environmentalStart, data.environmentalEnd);

        Vector2fc rainDistance = lerpFogDistance(cameraPos, world, baseRadius, WeatherEffectType::getRainWeatherData);

        Vector2fc thunderDistance = thunderGradient > 0
                ? lerpFogDistance(cameraPos, world, baseRadius, WeatherEffectType::getThunderWeatherData)
                : null;

        updateFogRadius(data, rainDistance, thunderDistance, rainGradient, thunderGradient, config);
    }

    public static boolean shouldApply(Level world) {
        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();
        return config.isEnableFogChanges()
                && world.getRainLevel(1f) > 0f
                && ((LevelAccessor) world).invokeCanHaveWeather();
    }

    private static Vector2fc lerpFogDistance(
            Vec3 pos,
            Level world,
            Vector2fc baseRadius,
            Function<WeatherEffectType, WeatherEffectType.WeatherData> fogDataSupplier
    ) {
        var samplePos = new BlockPos.MutableBlockPos();
        final var accumulator = new WeightedVector2fAccumulator();
        final int undergroundFogCutoff = world.getSeaLevel();

        // interpolate fog distances to make less jarring biome transition
        // start is stored in X and end in Y
        GaussianSampler.sample(
                pos,
                (x, y, z) -> {
                    samplePos.set(x, y, z);

                    if (y < undergroundFogCutoff) {
                        return baseRadius;
                    }

                    WeatherEffectType sampledType = WeatherEffects.getCurrentType(
                            world,
                            samplePos,
                            false,
                            (type, biomeHolder) -> {
                                return ImmersiveStormsClient.getConfig().isEnabled(type)
                                        && WeatherEffectsClient.typeAffectsBiome(type, biomeHolder);
                            }
                    );

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
        float fogEnd = Mth.lerp(rainGradient, data.environmentalEnd, rainDistance.y());

        if (thunderDistance != null) {
            fogEnd = Mth.lerp(thunderGradient, fogEnd, thunderDistance.y());
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