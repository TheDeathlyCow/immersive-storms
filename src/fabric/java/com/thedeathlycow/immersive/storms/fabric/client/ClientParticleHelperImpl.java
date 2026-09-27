package com.thedeathlycow.immersive.storms.fabric.client;

import com.thedeathlycow.immersive.storms.client.particle.ClientParticleHelper;
import com.thedeathlycow.immersive.storms.particle.ParticleHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public final class ClientParticleHelperImpl implements ClientParticleHelper {
    @Override
    public <T extends ParticleOptions> void registerProvider(ParticleType<T> type, ParticleHelper.ProviderFactory<T> provider) {
        ParticleProviderRegistry.getInstance().register(type, provider::create);
    }
}