package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.api.WeatherEffects;
import com.thedeathlycow.immersive.storms.api.WeatherEffectsClient;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
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

public final class StormEffects {
    private static final float START_FOG_SPHERE_RAIN_GRADIENT = 0.75f;

    private static final float FOG_START = 16f;

    private static final float FOG_END = 64f;

    private static final float PARTICLE_SCALE = 10f;

    public static boolean shouldCancelClouds(ClientWorld world, BlockPos pos) {
        return world.getRainGradient(1f) > START_FOG_SPHERE_RAIN_GRADIENT
                && WeatherEffectsClient.getCurrentType(world, pos, false) != WeatherEffects.Type.NONE;
    }

    public static Vec3d getFogColor(
            ClientWorld world,
            Camera camera,
            float baseRed, float baseGreen, float baseBlue,
            float tickProgress
    ) {
        float gradient = world.getRainGradient(tickProgress);
        if (gradient <= 0f) {
            return null;
        }

        WeatherEffects.Type currentEffects = WeatherEffectsClient.getCurrentType(world, camera.getBlockPos(), false);

        if (currentEffects != WeatherEffects.Type.NONE) {
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
                        RegistryEntry<Biome> biome = world.getBiome(samplePos);
                        WeatherEffects.Type sampledType = WeatherEffects.Type.forBiome(biome, ClientTags::isInWithLocalFallback);

                        return world.getDimensionEffects().adjustFogColor(
                                sampledType != WeatherEffects.Type.NONE
                                        ? ISMath.lerp(gradient, normalColor, sampledType.getColor())
                                        : normalColor,
                                skyAngle
                        );
                    }
            );
        }
        return null;
    }

    public static void updateFogDistance(
            Camera camera,
            BackgroundRenderer.FogType fogType,
            CameraSubmersionType cameraSubmersionType,
            BackgroundRenderer.FogData fogData,
            float tickProgress
    ) {
        if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
            return;
        }

        Entity focused = camera.getFocusedEntity();
        World world = focused.getWorld();
        final float rainGradient = world.getRainGradient(tickProgress);

        if (cameraSubmersionType == CameraSubmersionType.NONE && rainGradient > 0f) {
            BlockPos pos = camera.getBlockPos();
            WeatherEffects.Type currentEffects = WeatherEffectsClient.getCurrentType(world, pos, false);
            if (currentEffects != WeatherEffects.Type.NONE) {
                var samplePos = new BlockPos.Mutable();
                final var baseRadius = new Vec3d(fogData.fogStart, fogData.fogEnd, 0);
                final var fogRadius = new Vec3d(
                        FOG_START,
                        FOG_END,
                        0
                );

                // tri lerp fog distances to make less jarring biome transition
                // start is stored in X and end in Y
                Vec3d fogDistances = CubicSampler.sampleColor(camera.getPos(), (x, y, z) -> {
                    samplePos.set(x, y, z);
                    WeatherEffects.Type sampledType = WeatherEffects.Type.forBiome(
                            world.getBiome(samplePos),
                            ClientTags::isInWithLocalFallback
                    );

                    if (sampledType != WeatherEffects.Type.NONE) {
                        return fogRadius;
                    }
                    return baseRadius;
                });

                // lerp fog distances for smooth transition when weather changes
                updateFogRadius(fogData, fogDistances, rainGradient);
            }
        }
    }

    private static void updateFogRadius(BackgroundRenderer.FogData fogData, Vec3d fogDistances, float rainGradient) {
        fogData.fogStart = MathHelper.lerp(rainGradient, fogData.fogStart, (float) fogDistances.x);
        fogData.fogEnd = MathHelper.lerp(rainGradient, fogData.fogEnd, (float) fogDistances.y);

        if (rainGradient > START_FOG_SPHERE_RAIN_GRADIENT) {
            fogData.fogShape = FogShape.SPHERE;
        }
    }

    private StormEffects() {

    }
}