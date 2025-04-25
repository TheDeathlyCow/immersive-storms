package com.thedeathlycow.immersive.storms.world;

import com.google.common.base.Suppliers;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticleEffect;
import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class WindParticles implements ClientTickEvents.EndWorldTick {
    private static final float PARTICLE_SCALE = 5f;
    private static final float PARTICLE_VELOCITY = -0.5f;

    @Override
    public void onEndTick(ClientWorld clientWorld) {
        if (clientWorld.getTickManager().isFrozen()) {
            return;
        }

        final MinecraftClient gameClient = MinecraftClient.getInstance();
        final Camera camera = gameClient.gameRenderer.getCamera();
        if (camera == null) {
            return;
        }

        final BlockPos cameraPos = camera.getBlockPos();
        final BlockPos.Mutable pos = new BlockPos.Mutable();
        final int renderDistance = 20;

        for (int i = 0; i < 3; i++) {
            int x = cameraPos.getX() + clientWorld.random.nextBetween(-renderDistance, renderDistance);
            int z = cameraPos.getZ() + clientWorld.random.nextBetween(-renderDistance, renderDistance);
            int y = clientWorld.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            pos.set(x, y, z);

            ParticleColor color = ParticleColor.forBiome(clientWorld.getBiome(pos));

            if (color != null) {
                ParticleEffect particle = color.getParticle();
                clientWorld.addParticleClient(
                        particle,
                        pos.getX() + clientWorld.random.nextDouble(),
                        pos.getY() + clientWorld.random.nextDouble(),
                        pos.getZ() + clientWorld.random.nextDouble(),
                        PARTICLE_VELOCITY, 0, 0
                );
            }
        }
    }

    private enum ParticleColor {
        SNOWY(ISBiomeTags.HAS_SNOWY_WIND, () -> new DustGrainParticleEffect(
                0xffffff,
                PARTICLE_SCALE
        )),
        SANDY(ISBiomeTags.HAS_SANDY_WIND, () -> new DustGrainParticleEffect(
                SandstormParticles.COLOR,
                PARTICLE_SCALE
        ));

        private final TagKey<Biome> tag;
        private final Supplier<ParticleEffect> particle;

        ParticleColor(TagKey<Biome> tag, Supplier<ParticleEffect> particle) {
            this.tag = tag;
            this.particle = Suppliers.memoize(particle::get);
        }

        public ParticleEffect getParticle() {
            return particle.get();
        }

        @Nullable
        public static ParticleColor forBiome(RegistryEntry<Biome> biome) {
            for (ParticleColor value : values()) {
                if (ClientTags.isInWithLocalFallback(value.tag, biome)) {
                    return value;
                }
            }

            return null;
        }
    }
}