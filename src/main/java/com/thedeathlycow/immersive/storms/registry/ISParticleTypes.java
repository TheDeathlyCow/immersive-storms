package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ISParticleTypes {
    public static final ParticleType<DustGrainParticleEffect> DUST_GRAIN = register(
            "dust_grain",
            FabricParticleTypes.complex(
                    DustGrainParticleEffect.CODEC,
                    DustGrainParticleEffect.PACKET_CODEC
            )
    );

    public static void initialize() {
        ImmersiveStorms.LOGGER.debug("Initialized Immersive Storms particle types");
    }

    private static <T extends ParticleOptions> ParticleType<T> register(String name, ParticleType<T> particle) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, ImmersiveStorms.id(name), particle);
    }

    private ISParticleTypes() {

    }
}