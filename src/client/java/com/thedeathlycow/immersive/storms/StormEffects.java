package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.api.WeatherEffects;
import com.thedeathlycow.immersive.storms.api.WeatherEffectsClient;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import org.joml.Vector3f;

public final class StormEffects {

    private static final float START_FOG_SPHERE_RAIN_GRADIENT = 0.75f;

    private static final float PARTICLE_SCALE = 10f;

    public static boolean shouldCancelClouds(ClientWorld world, BlockPos pos) {
        return world.getRainGradient(1f) > START_FOG_SPHERE_RAIN_GRADIENT
                && WeatherEffectsClient.getCurrentType(world, pos, false) != WeatherEffects.Type.NONE;
    }

    public static Vec3d getFogColor(
            ClientWorld world,
            Camera camera,
            float baseRed, float baseGreen, float baseBlue,
            float tickDelta
    ) {
        float gradient = world.getRainGradient(1f);
        if (gradient <= 0f) {
            return null;
        }

        WeatherEffects.Type type = WeatherEffectsClient.getCurrentType(world, camera.getBlockPos(), false);

        if (type != WeatherEffects.Type.NONE) {
            final var normalColor = new Vec3d(baseRed, baseGreen, baseBlue);
            Vec3d adjustedColor = ISMath.lerp(gradient, normalColor, type.getColor());

            // idk why the game does this transformation but ill do it here too for consistency
            float skyAngle = MathHelper.clamp(
                    MathHelper.cos(world.getSkyAngle(tickDelta) * 2 * MathHelper.PI) * 2.0F + 0.5F,
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
                                        ? adjustedColor
                                        : normalColor,
                                skyAngle
                        );
                    }
            );
        }
        return null;
    }

    private StormEffects() {

    }
}