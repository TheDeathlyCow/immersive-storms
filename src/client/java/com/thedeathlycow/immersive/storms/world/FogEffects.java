package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.util.ISMath;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class FogEffects {
    private static final float START_FOG_SPHERE_RAIN_GRADIENT = 0.75f;

    public static Vec3d getFogColor(
            ClientWorld world,
            Camera camera,
            float baseRed, float baseGreen, float baseBlue,
            float tickProgress,
            ImmersiveStormsConfig config
    ) {
        float gradient = world.getRainGradient(tickProgress);
        if (!config.isEnableFogChanges() || gradient <= 0f) {
            return null;
        }

        final var normalColor = new Vec3d(baseRed, baseGreen, baseBlue);

        // idk why the game does this transformation but ill do it here too for consistency
        float skyAngle = MathHelper.clamp(
                MathHelper.cos(world.getSkyAngle(tickProgress) * 2 * MathHelper.PI) * 2.0F + 0.5F,
                0.0F, 1.0F
        );

        var samplePos = new BlockPos.Mutable();

        return CubicSampler.sampleColor(
                camera.getPos(),
                (x, y, z) -> {
                    samplePos.set(x, y, z);
                    RegistryEntry<Biome> biome = world.getBiomeAccess().getBiome(samplePos);
                    WeatherEffectType sampledType = WeatherEffectType.forBiome(biome, WeatherEffectsClient::isWeatherEffectTypeEnabled);
                    Vec3d color = sampledType.getColor();

                    return world.getDimensionEffects().adjustFogColor(
                            color != null
                                    ? ISMath.lerp(gradient, normalColor, color)
                                    : normalColor,
                            skyAngle
                    );
                }
        );
    }

    public static Fog updateFogDistance(
            Fog fog,
            Camera camera,
            BackgroundRenderer.FogType fogType,
            CameraSubmersionType cameraSubmersionType,
            float tickProgress,
            ImmersiveStormsConfig config
    ) {
        Entity focused = camera.getFocusedEntity();
        World world = focused.getWorld();
        final float rainGradient = world.getRainGradient(tickProgress);

        if (config.isEnableFogChanges() && cameraSubmersionType == CameraSubmersionType.NONE && rainGradient > 0f) {
            if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
                return rainGradient > START_FOG_SPHERE_RAIN_GRADIENT
                        ? new Fog(fog.start(), fog.end(), FogShape.CYLINDER, fog.red(), fog.green(), fog.blue(), fog.alpha())
                        : fog;
            }

            final var baseRadius = new Vec3d(fog.start(), fog.end(), 0);

            final float thunderGradient = world.getThunderGradient(tickProgress);

            Vec3d rainDistance = lerpFogDistance(camera.getPos(), world, baseRadius, WeatherEffectType::getRainWeatherData);

            Vec3d thunderDistance = thunderGradient > 0
                    ? lerpFogDistance(camera.getPos(), world, baseRadius, WeatherEffectType::getThunderWeatherData)
                    : null;

            // lerp fog distances for smooth transition when weather changes
            return updateFogRadius(fog, rainDistance, thunderDistance, rainGradient, thunderGradient, config);
        }

        return fog;
    }

    private static Vec3d lerpFogDistance(
            Vec3d pos,
            World world,
            Vec3d baseRadius,
            Function<WeatherEffectType, WeatherEffectType.WeatherData> fogDataSupplier
    ) {
        var samplePos = new BlockPos.Mutable();

        // tri lerp fog distances to make less jarring biome transition
        // start is stored in X and end in Y
        return CubicSampler.sampleColor(pos, (x, y, z) -> {
            samplePos.set(x, y, z);
            WeatherEffectType sampledType = WeatherEffectsClient.getCurrentType(world, samplePos, true);
            
            WeatherEffectType.WeatherData fogData = fogDataSupplier.apply(sampledType);
            if (fogData != null) {
                return fogData.fogDistance();
            }

            return baseRadius;
        });
    }

    private static Fog updateFogRadius(
            Fog fog,
            Vec3d rainDistance,
            @Nullable Vec3d thunderDistance,
            float rainGradient,
            float thunderGradient,
            ImmersiveStormsConfig config
    ) {
        float fogStart = MathHelper.lerp(rainGradient, fog.start(), (float) rainDistance.x);
        float fogEnd = MathHelper.lerp(rainGradient, fog.end(), (float) rainDistance.y);

        if (thunderDistance != null) {
            fogStart = MathHelper.lerp(thunderGradient, fogStart, (float) thunderDistance.x);
            fogEnd = MathHelper.lerp(thunderGradient, fogEnd, (float) thunderDistance.y);
        }

        fogStart *= config.getFogDistanceMultiplier();
        fogEnd *= config.getFogDistanceMultiplier();

        return new Fog(fogStart, fogEnd, fog.shape(), fog.red(), fog.green(), fog.blue(), fog.alpha());
    }

    private FogEffects() {

    }
}