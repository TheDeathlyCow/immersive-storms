package com.thedeathlycow.immersive.storms.client.world;

import com.google.common.base.Suppliers;
import com.thedeathlycow.immersive.storms.client.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.client.config.section.BiomeConfig;
import com.thedeathlycow.immersive.storms.client.config.section.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.client.util.ISClientTickEvents;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticleEffect;
import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.function.Supplier;

public class BiomeWindEffects implements ISClientTickEvents.EndLevel {
    private static final int PARTICLES_PER_TICK = 3;
    private static final float PARTICLE_SCALE = 4f;
    private static final Vector2d PARTICLE_VELOCITY = new Vector2d(-1.0, -1.0).normalize(0.6);
    private static final float PARTICLE_CHANCE = 1f / 5f;
    private static final float SOUND_CHANCE = 1f / 100f;

    @Override
    public void onEndTick(ClientLevel level) {
        if (level.tickRateManager().isFrozen()) {
            return;
        }

        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();
        boolean enableParticles = config.isEnableAmbientWindParticles();
        boolean enableSounds = config.isEnableAmbientWindSounds();

        if (!(enableParticles || enableSounds)) {
            return;
        }

        final Minecraft gameClient = Minecraft.getInstance();
        final Camera camera = gameClient.gameRenderer.getMainCamera();

        final BlockPos cameraPos = camera.blockPosition();
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final int particleRenderDistance = 20;

        final RandomSource random = level.getRandom();

        for (int i = 0; i < PARTICLES_PER_TICK; i++) {
            setRandomTopPos(level, cameraPos, random, particleRenderDistance, pos);

            ParticleColor color = ParticleColor.at(level, pos);

            if (color != null) {
                addParticleAndSound(level, config, random, color, pos, cameraPos);
            }
        }
    }

    private static void addParticleAndSound(
            ClientLevel level,
            ImmersiveStormsConfig config,
            RandomSource random,
            ParticleColor color,
            BlockPos.MutableBlockPos pos,
            BlockPos cameraPos
    ) {
        boolean enableParticles = config.isEnableAmbientWindParticles();
        boolean enableSounds = config.isEnableAmbientWindSounds();

        boolean addParticle = enableParticles
                && random.nextFloat() < PARTICLE_CHANCE * config.getWindParticleChanceMultiplier();

        if (addParticle) {
            ParticleOptions particle = color.getParticle();
            double speed = random.triangle(1.0, 0.5);

            level.addParticle(
                    particle,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + random.nextDouble() * 0.2 + 0.15,
                    pos.getZ() + random.nextDouble(),
                    speed * PARTICLE_VELOCITY.x, 0, speed * PARTICLE_VELOCITY.y
            );
        }

        boolean tryPlaySound = enableSounds
                && !level.isRaining()
                && random.nextFloat() < SOUND_CHANCE;

        if (tryPlaySound) {
            final int soundDistance = 5;

            setRandomTopPos(level, cameraPos, random, soundDistance, pos);
            boolean canPlaySound = Math.abs(pos.getY() - cameraPos.getY()) <= soundDistance * 2;
            if (canPlaySound) {
                level.playLocalSound(
                        pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.DRY_GRASS, // this is actually a generic wind sound
                        SoundSource.AMBIENT,
                        1.0f, 1.0f,
                        true
                );
            }
        }
    }

    private static void setRandomTopPos(
            ClientLevel level,
            BlockPos center,
            RandomSource random,
            int offset,
            BlockPos.MutableBlockPos out
    ) {
        int x = center.getX() + random.nextIntBetweenInclusive(-offset, offset);
        int z = center.getZ() + random.nextIntBetweenInclusive(-offset, offset);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        out.set(x, y, z);
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
        private final Supplier<ParticleOptions> particle;

        ParticleColor(TagKey<Biome> tag, Supplier<ParticleOptions> particle) {
            this.tag = tag;
            this.particle = Suppliers.memoize(particle::get);
        }

        public ParticleOptions getParticle() {
            return particle.get();
        }

        @Nullable
        public static ParticleColor at(Level level, BlockPos pos) {
            if (level.environmentAttributes().getValue(ISEnvironmentAttributes.HAS_AMBIENT_WIND, pos)) {
                Holder<Biome> biome = level.getBiomeManager().getNoiseBiomeAtPosition(pos);

                // TODO: remove tag usage here, it does not work client-side
                // maybe use an attribute for color?
                for (ParticleColor value : values()) {
                    if (biome.is(value.tag)) {
                        return value;
                    }
                }

                return ROCKY;
            }

            return null;
        }
    }
}