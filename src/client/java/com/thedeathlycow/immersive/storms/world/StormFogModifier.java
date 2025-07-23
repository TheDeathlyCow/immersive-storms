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
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class StormFogModifier {
    public static Vec3d sampleWeatherFogColor(
            ClientWorld world,
            Vec3d pos,
            ToIntFunction<Biome> biomeColorSupplier
    ) {
        final float rainGradient = world.getRainGradient(1f);

        return CubicSampler.sampleColor(
                pos,
                (x, y, z) -> {
                    RegistryEntry<Biome> biome = world.getBiomeAccess().getBiomeForNoiseGen(x, y, z);
                    WeatherEffectType sampledType = WeatherEffectType.forBiome(biome, WeatherEffectsClient::isWeatherEffectTypeEnabled);

                    Vec3d biomeColor = Vec3d.unpackRgb(biomeColorSupplier.applyAsInt(biome.value()));
                    int color = sampledType.getColor();

                    if (color >= 0) {
                        return ISMath.lerp(rainGradient, biomeColor, Vec3d.unpackRgb(color));
                    } else {
                        return biomeColor;
                    }
                }
        );
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

        var baseRadius = new Vec3d(data.environmentalStart, data.environmentalEnd, 0);

        Vec3d rainDistance = lerpFogDistance(cameraPos, world, baseRadius, WeatherEffectType::getRainWeatherData);

        Vec3d thunderDistance = thunderGradient > 0
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

    private static Vec3d lerpFogDistance(
            Vec3d pos,
            World world,
            Vec3d baseRadius,
            Function<WeatherEffectType, WeatherEffectType.WeatherData> fogDataSupplier
    ) {
        var samplePos = new BlockPos.Mutable();
        final int undergroundFogCutoff = world.getSeaLevel();

        // tri lerp fog distances to make less jarring biome transition
        // start is stored in X and end in Y
        return CubicSampler.sampleColor(pos, (x, y, z) -> {
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
        });
    }

    private static void updateFogRadius(
            FogData data,
            Vec3d rainDistance,
            @Nullable Vec3d thunderDistance,
            float rainGradient,
            float thunderGradient,
            ImmersiveStormsConfig config
    ) {
        float fogEnd = MathHelper.lerp(rainGradient, data.environmentalEnd, (float) rainDistance.y);

        if (thunderDistance != null) {
            fogEnd = MathHelper.lerp(thunderGradient, fogEnd, (float) thunderDistance.y);
        }

        fogEnd *= config.getFogDistanceMultiplier();

        if (fogEnd < data.environmentalEnd) {
            float reduction = fogEnd / data.environmentalEnd;

            data.environmentalEnd = fogEnd;
            data.skyEnd *= reduction;
            data.cloudEnd *= reduction;

            if (ImmersiveStormsClient.isDistantHorizonsLoaded()) {
                setFogDistanceForDistantHorizons(reduction);
            }
        }
    }

    private static void setFogDistanceForDistantHorizons(double reduction) {
        IDhApiConfig config = DhApi.Delayed.configs;
        if (config != null && reduction < 1) {
            config.graphics().fog().farFog().farFogStartDistance().setValue(reduction * 0.1);
            config.graphics().fog().farFog().farFogEndDistance().setValue(reduction * 0.1);
        }
    }

    private StormFogModifier() {

    }
}