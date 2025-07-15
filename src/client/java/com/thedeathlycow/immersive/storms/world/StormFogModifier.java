package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.mixin.client.WorldAccessor;
import com.thedeathlycow.immersive.storms.util.ISMath;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import net.minecraft.client.render.Camera;
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

public class StormFogModifier {
    public void applyStartEndModifier(
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

    public int modifyFogColor(
            ClientWorld world,
            Camera camera,
            float skyDarkness,
            int baseFogColor
    ) {
        if (!this.shouldApply(world)) {
            return baseFogColor;
        }

        RegistryEntry<Biome> biome = world.getBiomeAccess().getBiome(camera.getBlockPos());
        WeatherEffectType sampledType = WeatherEffectType.forBiome(biome, WeatherEffectsClient::isWeatherEffectTypeEnabled);

        int sampledColor = sampledType.getColor();

        if (sampledColor < 0) {
            return baseFogColor;
        }

        Vec3d mixed = ISMath.lerp(0.5f, Vec3d.unpackRgb(sampledColor), Vec3d.unpackRgb(baseFogColor)).multiply(255);

        return ((int)mixed.x << 16) | ((int)mixed.y << 8) | ((int)mixed.z);
    }

    public boolean shouldApply(World world) {
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

    private static void updateFogRadius(
            FogData data,
            Vec3d rainDistance,
            @Nullable Vec3d thunderDistance,
            float rainGradient,
            float thunderGradient,
            ImmersiveStormsConfig config
    ) {
        float fogStart = MathHelper.lerp(rainGradient, data.environmentalStart, (float) rainDistance.x);
        float fogEnd = MathHelper.lerp(rainGradient, data.environmentalEnd, (float) rainDistance.y);

        if (thunderDistance != null) {
            fogStart = MathHelper.lerp(thunderGradient, fogStart, (float) thunderDistance.x);
            fogEnd = MathHelper.lerp(thunderGradient, fogEnd, (float) thunderDistance.y);
        }

        fogStart *= config.getFogDistanceMultiplier();
        fogEnd *= config.getFogDistanceMultiplier();

        data.environmentalStart = fogStart;
        data.environmentalEnd = fogEnd;
    }
}