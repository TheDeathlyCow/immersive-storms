package com.thedeathlycow.immersive.storms.particle;

import com.mojang.serialization.MapCodec;
import com.thedeathlycow.immersive.storms.util.ServicesHelper;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ParticleHelper {
    <T extends ParticleOptions> ParticleType<T> createComplex(
            MapCodec<T> codec,
            StreamCodec<RegistryFriendlyByteBuf, T> networkCodec
    );

    SimpleParticleType createSimple(boolean alwaysSpawn);

    static ParticleHelper getInstance() {
        return ServicesHelper.PARTICLE_HELPER;
    }

    @FunctionalInterface
    interface ProviderFactory<T extends ParticleOptions> {
        ParticleProvider<T> create(SpriteSet spriteSet);
    }
}