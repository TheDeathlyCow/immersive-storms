package com.thedeathlycow.immersive.storms.block;

import com.google.common.base.Suppliers;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticleEffect;
import com.thedeathlycow.immersive.storms.registry.ISBlockTags;
import com.thedeathlycow.immersive.storms.world.SandstormParticles;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.function.Supplier;

public final class BlockWindParticle implements RandomBlockDisplayTickCallback {
    private static final float PARTICLE_SCALE = 5f;
    //    private static final int SOUND_CHANCE = 10_000;
    private static final int PARTICLE_CHANCE = 60;
    private static final Vector2f PARTICLE_VELOCITY = new Vector2f(-1f, -1f).normalize().mul(0.6f);

    @Override
    public void randomDisplayTick(ClientWorld world, BlockState state, BlockPos pos, Random random) {
        ParticleColor color = ParticleColor.forBlock(state.getBlock());
        if (color != null && world.isSkyVisible(pos.up())) {
//            if (random.nextInt(SOUND_CHANCE) == 0 && shouldPlayAmbienceAt(world, pos)) {
//                world.playSoundClient(
//                        pos.getX(), pos.getY(), pos.getZ(),
//                        SoundEvents.BLOCK_SAND_WIND, // this is actually a generic wind sound
//                        SoundCategory.AMBIENT,
//                        1.0f, 1.0f,
//                        false
//                );
//            }


            if (random.nextInt(PARTICLE_CHANCE) == 0 && shouldPlayAmbienceAt(world, pos, color.tag)) {
                int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());

                world.addParticleClient(
                        color.getParticle(),
                        pos.getX() + world.random.nextDouble(),
                        y + world.random.nextDouble(),
                        pos.getZ() + world.random.nextDouble(),
                        PARTICLE_VELOCITY.x, 0, PARTICLE_VELOCITY.y
                );
            }
        }
    }

    public static boolean shouldPlayAmbienceAt(World world, BlockPos pos, TagKey<Block> tag) {
        int i = 0;

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos.withY(world.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos) - 1));
            if (blockState.isIn(tag)) {
                if (++i >= 3) {
                    return true;
                }
            }
        }

        return false;
    }

    private enum ParticleColor {
        SNOWY(ISBlockTags.PRODUCES_AMBIENT_SNOWY_WIND_PARTICLE, () -> new DustGrainParticleEffect(
                0xffffff,
                PARTICLE_SCALE
        )),
        SANDY(ISBlockTags.PRODUCES_AMBIENT_SANDY_WIND_PARTICLE, () -> new DustGrainParticleEffect(
                SandstormParticles.COLOR,
                PARTICLE_SCALE
        ));

        private final TagKey<Block> tag;
        private final Supplier<ParticleEffect> particle;

        ParticleColor(TagKey<Block> tag, Supplier<ParticleEffect> particle) {
            this.tag = tag;
            this.particle = Suppliers.memoize(particle::get);
        }

        public ParticleEffect getParticle() {
            return particle.get();
        }

        @Nullable
        public static ParticleColor forBlock(Block block) {
            for (ParticleColor value : values()) {
                if (ClientTags.isInWithLocalFallback(value.tag, block)) {
                    return value;
                }
            }

            return null;
        }
    }
}