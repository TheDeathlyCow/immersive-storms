package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

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

    private static <T extends ParticleEffect> ParticleType<T> register(String name, ParticleType<T> particle) {
        return Registry.register(Registries.PARTICLE_TYPE, ImmersiveStorms.id(name), particle);
    }

    private ISParticleTypes() {

    }
}