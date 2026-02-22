package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.config.Updater;
import com.thedeathlycow.immersive.storms.config.section.BiomeConfig;
import com.thedeathlycow.immersive.storms.config.section.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.config.section.SandstormConfig;
import com.thedeathlycow.immersive.storms.particle.DustGrainParticle;
import com.thedeathlycow.immersive.storms.particle.PaleWaterDropParticle;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import com.thedeathlycow.immersive.storms.world.BiomeWindEffects;
import com.thedeathlycow.immersive.storms.world.SandstormParticles;
import com.thedeathlycow.immersive.storms.world.SandstormSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.loader.api.FabricLoader;

public class ImmersiveStormsClient implements ClientModInitializer {
    private static boolean isDistantHorizonsLoaded = false;

    @Override
    public void onInitializeClient() {
        registerConfig();
        checkDistantHorizons();

        boolean disableSandstormEffects = getConfig().getSandstorm().isDetectParticleRain()
                && FabricLoader.getInstance().isModLoaded("particlerain");

        if (!disableSandstormEffects) {
            ClientTickEvents.END_WORLD_TICK.register(new SandstormParticles());
            ClientTickEvents.END_WORLD_TICK.register(new SandstormSounds());
        } else {
            ImmersiveStorms.LOGGER.info("Particle Rain has been detected, disabling Immersive Storms sandstorm particle and sound effects");
        }

        ClientTickEvents.END_WORLD_TICK.register(new BiomeWindEffects());

        ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
        particleRegistry.register(ISParticleTypes.DUST_GRAIN, DustGrainParticle.Provider::new);
        particleRegistry.register(ISParticleTypes.PALE_RAIN, PaleWaterDropParticle.Provider::new);
    }

    public static ImmersiveStormsConfig getConfig() {
        return ImmersiveStormsConfig.HANDLER.instance();
    }

    public static boolean isDistantHorizonsLoaded() {
        return isDistantHorizonsLoaded;
    }

    private static void registerConfig() {
        Updater.initialize();
        ImmersiveStormsConfig.HANDLER.load();
        ImmersiveStormsConfig.HANDLER.save();
        SandstormConfig.HANDLER.load();
        SandstormConfig.HANDLER.save();
        BiomeConfig.HANDLER.load();
        BiomeConfig.HANDLER.save();
    }

    private static void checkDistantHorizons() {
        isDistantHorizonsLoaded = FabricLoader.getInstance().isModLoaded("distanthorizons");
    }
}