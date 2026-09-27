package com.thedeathlycow.immersive.storms.client.particle;

import com.thedeathlycow.immersive.storms.client.util.ClientServicesHelper;
import com.thedeathlycow.immersive.storms.particle.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public interface ClientParticleHelper {
    <T extends ParticleOptions> void registerProvider(ParticleType<T> type, ParticleHelper.ProviderFactory<T> provider);

    static ClientParticleHelper getInstance() {
        return ClientServicesHelper.CLIENT_PARTICLE_HELPER;
    }
}