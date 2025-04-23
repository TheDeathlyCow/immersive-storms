package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticle;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import com.thedeathlycow.immersive.storms.world.SandstormParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ImmersiveStormsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register(new SandstormParticles());

        ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
        particleRegistry.register(ISParticleTypes.DUST_GRAIN, DustGrainParticle.Factory::new);
    }

    public static ImmersiveStormsConfig getConfig() {
        return ImmersiveStorms.getConfig();
    }
}