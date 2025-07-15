package com.thedeathlycow.immersive.storms.world;

import com.google.common.base.Suppliers;
import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.mixin.client.AmbientDesertBlockSoundsAccessor;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticleEffect;
import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.block.ShortDryGrassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.function.Supplier;

public class BiomeWindEffects implements ClientTickEvents.EndWorldTick {
    private static final int PARTICLES_PER_TICK = 3;
    private static final float PARTICLE_SCALE = 4f;
    private static final Vector2d PARTICLE_VELOCITY = new Vector2d(-1.0, -1.0).normalize(0.6);
    private static final float PARTICLE_CHANCE = 1f / 5f;
    private static final float SOUND_CHANCE = 1f / 100f;

    @Override
    public void onEndTick(ClientWorld clientWorld) {
        if (clientWorld.getTickManager().isFrozen()) {
            return;
        }

        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();
        boolean enableParticles = config.isEnableAmbientWindParticles();
        boolean enableSounds = config.isEnableAmbientWindSounds();

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
        final int particleRenderDistance = 20;

        final Random random = clientWorld.getRandom();

        for (int i = 0; i < PARTICLES_PER_TICK; i++) {
            RegistryEntry<Biome> biome = setRandomTopPos(clientWorld, cameraPos, random, particleRenderDistance, pos);

            ParticleColor color = ParticleColor.forBiome(biome);

            if (color != null) {
                addParticleAndSound(clientWorld, config, random, color, pos, cameraPos);
            }
        }
    }

    private static void addParticleAndSound(
            ClientWorld clientWorld,
            ImmersiveStormsConfig config,
            Random random,
            ParticleColor color,
            BlockPos.Mutable pos,
            BlockPos cameraPos
    ) {
        boolean enableParticles = config.isEnableAmbientWindParticles();
        boolean enableSounds = config.isEnableAmbientWindSounds();

        boolean addParticle = enableParticles
                && random.nextFloat() < PARTICLE_CHANCE * config.getWindParticleChanceMultiplier();

        if (addParticle) {
            ParticleEffect particle = color.getParticle();
            double speed = random.nextTriangular(1.0, 0.5);

            clientWorld.addParticleClient(
                    particle,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble() * 0.2 + 0.15,
                    pos.getZ() + random.nextDouble(),
                    speed * PARTICLE_VELOCITY.x, 0, speed * PARTICLE_VELOCITY.y
            );
        }

        boolean tryPlaySound = enableSounds
                && !clientWorld.isRaining()
                && random.nextFloat() < SOUND_CHANCE;

        if (tryPlaySound) {
            final int soundDistance = 5;

            RegistryEntry<Biome> biome = setRandomTopPos(clientWorld, cameraPos, random, soundDistance, pos);


            boolean canPlaySound = Math.abs(pos.getY() - cameraPos.getY()) <= soundDistance * 2
                    && !AmbientDesertBlockSoundsAccessor.invokeShouldPlayWindSoundIn(biome); // avoid overlap with vanilla wind sound from sand
            if (canPlaySound) {
                clientWorld.playSoundClient(
                        pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.BLOCK_DRY_GRASS_AMBIENT, // this is actually a generic wind sound
                        SoundCategory.AMBIENT,
                        1.0f, 1.0f,
                        true
                );
            }
        }
    }

    private static RegistryEntry<Biome> setRandomTopPos(
            ClientWorld world,
            BlockPos center,
            Random random,
            int offset,
            BlockPos.Mutable out
    ) {
        int x = center.getX() + random.nextBetween(-offset, offset);
        int z = center.getZ() + random.nextBetween(-offset, offset);
        int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        out.set(x, y, z);
        return world.getBiomeAccess().getBiomeForNoiseGen(out);
    }

    private enum ParticleColor {
        SNOWY(ISBiomeTags.HAS_BLIZZARDS, () -> new DustGrainParticleEffect(
                0xffffff,
                PARTICLE_SCALE
        )),
        SANDY(ISBiomeTags.HAS_SANDSTORMS, () -> new DustGrainParticleEffect(
                SandstormParticles.COLOR,
                PARTICLE_SCALE
        )),
        ROCKY(ISBiomeTags.IS_WINDY, () -> new DustGrainParticleEffect(
                0x888888,
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
            if (ClientTags.isInWithLocalFallback(ISBiomeTags.IS_WINDY, biome)) {
                for (ParticleColor value : values()) {
                    if (ClientTags.isInWithLocalFallback(value.tag, biome)) {
                        return value;
                    }
                }

                return ROCKY;
            }

            return null;
        }
    }
}