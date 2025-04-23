package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.api.WeatherEffectType;
import com.thedeathlycow.immersive.storms.api.WeatherEffectsClient;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticleEffect;
import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import org.joml.Vector3f;

import java.util.Objects;

public class SandstormParticles implements ClientTickEvents.EndWorldTick {
    private static final float PARTICLE_SCALE = 10f;
    private static final float PARTICLE_VELOCITY = -1f;
    private static final int PARTICLE_RARITY = 60;

    private final Vector3f color = Objects.requireNonNull(WeatherEffectType.SANDSTORM.getColor()).toVector3f();

    @Override
    public void onEndTick(ClientWorld clientWorld) {
        if (!clientWorld.isRaining() || clientWorld.getTickManager().isFrozen()) {
            return;
        }

        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();
        final int renderDistance = 20;
        if (renderDistance <= 0) {
            return; // config disabled
        }

        final MinecraftClient gameClient = MinecraftClient.getInstance();
        final Camera camera = gameClient.gameRenderer.getCamera();
        if (camera == null) {
            return; // no camera for whatever reason
        }

        // main particle loop
        final BlockPos cameraPos = camera.getBlockPos();
        final BlockPos.Mutable pos = new BlockPos.Mutable();
        final ParticleEffect particle = new DustGrainParticleEffect(color, PARTICLE_SCALE);
        final int rarity = PARTICLE_RARITY;
        final int cameraY = cameraPos.getY();
        final float particleVelocity = PARTICLE_VELOCITY;

        for (int x = cameraPos.getX() - renderDistance; x < cameraPos.getX() + renderDistance; x++) {
            for (int z = cameraPos.getZ() - renderDistance; z < cameraPos.getZ() + renderDistance; z++) {
                int y = cameraY + clientWorld.random.nextBetween(-renderDistance / 2, (renderDistance + 1) / 2);
                y = Math.max(y, clientWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z));
                pos.set(x, y, z);
                addParticle(clientWorld, particle, pos, rarity, particleVelocity);
            }
        }
    }

    private static void addParticle(ClientWorld world, ParticleEffect particle, BlockPos pos, int rarity, float velocity) {
        boolean addParticle = world.random.nextInt(rarity) == 0
                && !world.hasRain(pos) // for compatibility with seasons mods
                && ClientTags.isInWithLocalFallback(ISBiomeTags.HAS_SANDSTORMS, world.getBiome(pos)); // faster than checking the weather effects api

        if (addParticle) {
            world.addParticleClient(
                    particle,
                    pos.getX() + world.random.nextDouble(),
                    pos.getY() + world.random.nextDouble(),
                    pos.getZ() + world.random.nextDouble(),
                    velocity, 0, 0
            );
        }
    }
}