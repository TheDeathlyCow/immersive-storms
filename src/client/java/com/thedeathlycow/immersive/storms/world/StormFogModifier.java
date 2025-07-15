package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import net.minecraft.block.Block;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
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

public class StormFogModifier extends FogModifier {
    @Override
    public void applyStartEndModifier(
            FogData data,
            Entity cameraEntity,
            BlockPos cameraPos,
            ClientWorld world,
            float viewDistance,
            RenderTickCounter tickCounter
    ) {
        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();

        final float tickProgress = tickCounter.getTickProgress(false);
        final float rainGradient = world.getRainGradient(tickProgress);
        final float thunderGradient = world.getThunderGradient(tickProgress);

        var baseRadius = new Vec3d(data.environmentalStart, data.environmentalEnd, 0);

        Vec3d cameraCenterPos = cameraPos.toCenterPos();
        Vec3d rainDistance = lerpFogDistance(cameraCenterPos, world, baseRadius, WeatherEffectType::getRainWeatherData);

        Vec3d thunderDistance = thunderGradient > 0
                ? lerpFogDistance(cameraCenterPos, world, baseRadius, WeatherEffectType::getThunderWeatherData)
                : null;

        updateFogRadius(data, rainDistance, thunderDistance, rainGradient, thunderGradient, config);
    }

    @Override
    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        BlockPos cameraPos = camera.getBlockPos();

        RegistryEntry<Biome> biome = world.getBiomeAccess().getBiome(cameraPos);
        WeatherEffectType sampledType = WeatherEffectType.forBiome(biome, WeatherEffectsClient::isWeatherEffectTypeEnabled);

        return sampledType.getColor();
    }

    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();
        World world = cameraEntity.getWorld();

        return config.isEnableFogChanges() && submersionType == CameraSubmersionType.ATMOSPHERIC && world.isRaining();
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