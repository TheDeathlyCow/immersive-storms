package com.thedeathlycow.immersive.storms.world;

import com.google.common.base.Suppliers;
import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.mixin.client.AmbientDesertBlockSoundsAccessor;
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
import net.minecraft.sound.AmbientDesertBlockSounds;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BiomeWindEffects implements ClientTickEvents.EndWorldTick {
    private static final int PARTICLES_PER_TICK = 3;
    private static final float PARTICLE_SCALE = 5f;
    private static final float PARTICLE_VELOCITY = -0.5f;
    private static final float PARTICLE_CHANCE = 1f / 3f;
    private static final float SOUND_CHANCE = 1f / 600f;

    @Override
    public void onEndTick(ClientWorld clientWorld) {
        if (clientWorld.getTickManager().isFrozen()) {
            return;
        }

        boolean enableParticles = ImmersiveStormsClient.getConfig().isEnableAmbientWindParticles();
        boolean enableSounds = ImmersiveStormsClient.getConfig().isEnableAmbientWindSounds();

        if (!(enableParticles || enableSounds)) {
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
        final Random random = clientWorld.getRandom();

        for (int i = 0; i < PARTICLES_PER_TICK; i++) {
            int x = cameraPos.getX() + random.nextBetween(-renderDistance, renderDistance);
            int z = cameraPos.getZ() + random.nextBetween(-renderDistance, renderDistance);
            int y = clientWorld.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            pos.set(x, y, z);

            RegistryEntry<Biome> biome = clientWorld.getBiome(pos);
            ParticleColor color = ParticleColor.forBiome(biome);

            if (color != null) {
                boolean addParticle = enableParticles
                        && random.nextFloat() < PARTICLE_CHANCE;

                if (addParticle) {
                    ParticleEffect particle = color.getParticle();
                    clientWorld.addParticleClient(
                            particle,
                            pos.getX() + random.nextDouble(),
                            pos.getY() + random.nextDouble(),
                            pos.getZ() + random.nextDouble(),
                            PARTICLE_VELOCITY, 0, 0
                    );
                }

                boolean playSound = enableSounds
                        && random.nextFloat() < SOUND_CHANCE
                        && !AmbientDesertBlockSoundsAccessor.invokeShouldPlayWindSoundIn(biome);

                if (playSound) {
                    clientWorld.playSoundClient(
                            pos.getX(), pos.getY(), pos.getZ(),
                            SoundEvents.BLOCK_SAND_WIND, // this is actually a generic wind sound
                            SoundCategory.AMBIENT,
                            1.0f, 1.0f,
                            false
                    );
                }
            }
        }
    }

    private enum ParticleColor {
        ROCKY(ISBiomeTags.IS_WINDY, () -> new DustGrainParticleEffect(
                0x888888,
                PARTICLE_SCALE
        )),
        SNOWY(ISBiomeTags.HAS_BLIZZARDS, () -> new DustGrainParticleEffect(
                0xffffff,
                PARTICLE_SCALE
        )),
        SANDY(ISBiomeTags.HAS_SANDSTORMS, () -> new DustGrainParticleEffect(
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