package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.config.section.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.config.section.SandstormConfig;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticleEffect;
import com.thedeathlycow.immersive.storms.util.ISMath;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector3f;

public final class SandstormParticles implements ClientTickEvents.EndWorldTick {
    public static final Vector3f COLOR = ISMath.unpackRgb(0xD9AA84);

    private static final float PARTICLE_SCALE = 10f;
    private static final float PARTICLE_VELOCITY = -1f;
    private static final float BASE_PARTICLE_CHANCE = 1f / 60f;

    @Override
    public void onEndTick(ClientLevel clientWorld) {
        if (!clientWorld.isRaining() || clientWorld.tickRateManager().isFrozen()) {
            return;
        }

        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();
        SandstormConfig sandstormConfig = config.getSandstorm();
        final int renderDistance = sandstormConfig.getSandstormParticleRenderDistance();
        boolean enabled = sandstormConfig.isEnableSandstormParticles()
                && renderDistance > 0;

        if (!enabled) {
            return;
        }

        final Minecraft gameClient = Minecraft.getInstance();
        final Camera camera = gameClient.gameRenderer.getMainCamera();
        if (camera == null) {
            return; // no camera for whatever reason
        }

        // main particle loop
        final BlockPos cameraPos = camera.blockPosition();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final ParticleOptions particle = new DustGrainParticleEffect(COLOR, PARTICLE_SCALE);
        final float rarity = BASE_PARTICLE_CHANCE * sandstormConfig.getSandstormParticleDensityMultiplier();
        final int cameraY = cameraPos.getY();
        final int xOffset = renderDistance / 2;

        for (int x = cameraPos.getX() - renderDistance; x < cameraPos.getX() + renderDistance; x++) {
            for (int z = cameraPos.getZ() - renderDistance; z < cameraPos.getZ() + renderDistance; z++) {
                // adjust to account for the fact that particles travel along the x-axis
                // makes the area the particles come from look less empty
                int adjustedX = x + xOffset;

                int y = cameraY + clientWorld.random.nextIntBetweenInclusive(-renderDistance / 2, (renderDistance + 1) / 2);
                y = Math.max(y, clientWorld.getHeight(Heightmap.Types.MOTION_BLOCKING, adjustedX, z));

                pos.set(adjustedX, y, z);
                addParticle(clientWorld, particle, pos, rarity);
            }
        }
    }

    private static void addParticle(ClientLevel world, ParticleOptions particle, BlockPos pos, float rarity) {
        Holder<Biome> biome = world.getBiomeManager().getNoiseBiomeAtPosition(pos);

        boolean addParticle = world.random.nextFloat() < rarity
                && biome.value().getPrecipitationAt(pos, world.getSeaLevel()) == Biome.Precipitation.NONE
                && WeatherEffectsClient.typeAffectsBiome(WeatherEffectType.SANDSTORM, biome);

        if (addParticle) {
            world.addParticle(
                    particle,
                    pos.getX() + world.random.nextDouble(),
                    pos.getY() + world.random.nextDouble(),
                    pos.getZ() + world.random.nextDouble(),
                    PARTICLE_VELOCITY, 0, 0
            );
        }
    }
}