package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticle;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import com.thedeathlycow.immersive.storms.world.SandstormParticles;
import com.thedeathlycow.immersive.storms.world.SandstormSounds;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersiveStormsClient implements ClientModInitializer {
    private static ConfigHolder<ImmersiveStormsConfig> configHolder = null;

    @Override
    public void onInitializeClient() {
        registerConfig();

        boolean disableSandstormEffects = getConfig().getSandstorm().isDetectParticleRain()
                && FabricLoader.getInstance().isModLoaded("particlerain");

        if (!disableSandstormEffects) {
            ClientTickEvents.END_WORLD_TICK.register(new SandstormParticles());
            ClientTickEvents.END_WORLD_TICK.register(new SandstormSounds());
        } else {
            ImmersiveStorms.LOGGER.info("Particle Rain has been detected, disabling Immersive Storms sandstorm particle and sound effects");
        }

        ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
        particleRegistry.register(ISParticleTypes.DUST_GRAIN, DustGrainParticle.Factory::new);
    }

    public static ImmersiveStormsConfig getConfig() {
        if (configHolder == null) {
            registerConfig();
        }

        return configHolder.getConfig();
    }

    private static void registerConfig() {
        configHolder = AutoConfig.register(ImmersiveStormsConfig.class, JanksonConfigSerializer::new);
    }
}