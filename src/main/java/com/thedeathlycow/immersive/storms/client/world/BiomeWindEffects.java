package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.client.util.ISClientTickEvents;
import com.thedeathlycow.immersive.storms.config.section.GeneralSettings;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticleEffect;
import com.thedeathlycow.immersive.storms.registry.ISEnvironmentAttributes;
import com.thedeathlycow.immersive.storms.registry.tag.ISBiomeTags;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
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

        GeneralSettings config = ImmersiveStormsClient.getConfig();
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
            GeneralSettings config,
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
            ParticleOptions particle = color.getParticle(config.windParticleSize());
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

    public enum ParticleColor {
        SNOWY(0xffffff),
        SANDY(0xD9AA84),
        ROCKY(0x888888);

        private final int color;

        ParticleColor(int color) {
            this.color = color;
        }

        public ParticleOptions getParticle(float size) {
            return new DustGrainParticleEffect(this.color, PARTICLE_SCALE * size);
        }

        @Nullable
        public static ParticleColor at(Level level, BlockPos pos) {
            if (level.environmentAttributes().getValue(ISEnvironmentAttributes.HAS_AMBIENT_WIND, pos)) {
                WeatherArchetype archetype = level.environmentAttributes().getValue(ISEnvironmentAttributes.WEATHER_ARCHETYPE, pos);
                return map(archetype);
            }

            return null;
        }

        public static ParticleColor map(WeatherArchetype archetype) {
            return switch (archetype) {
                case SANDSTORM -> SANDY;
                case BLIZZARD -> SNOWY;
                default -> ROCKY;
            };
        }

        public int color() {
            return this.color;
        }
    }
}