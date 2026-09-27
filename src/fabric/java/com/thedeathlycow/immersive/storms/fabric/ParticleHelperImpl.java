package com.thedeathlycow.immersive.storms.fabric;

import com.mojang.serialization.MapCodec;
import com.thedeathlycow.immersive.storms.particle.ParticleHelper;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class ParticleHelperImpl implements ParticleHelper {
    @Override
    public <T extends ParticleOptions> ParticleType<T> createComplex(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> networkCodec) {
        return FabricParticleTypes.complex(codec, networkCodec);
    }

    @Override
    public SimpleParticleType createSimple(boolean alwaysSpawn) {
        return FabricParticleTypes.simple(alwaysSpawn);
    }
}