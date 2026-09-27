package com.thedeathlycow.immersive.storms.client;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.client.config.Updater;
import com.thedeathlycow.immersive.storms.client.config.section.BiomeConfig;
import com.thedeathlycow.immersive.storms.client.config.section.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.client.config.section.SandstormConfig;
import com.thedeathlycow.immersive.storms.client.particle.BlackWaterDropParticle;
import com.thedeathlycow.immersive.storms.client.particle.ClientParticleHelper;
import com.thedeathlycow.immersive.storms.client.particle.DustGrainParticle;
import com.thedeathlycow.immersive.storms.client.util.ISClientTickEvents;
import com.thedeathlycow.immersive.storms.client.world.BiomeWindEffects;
import com.thedeathlycow.immersive.storms.client.world.SandstormParticles;
import com.thedeathlycow.immersive.storms.client.world.SandstormSounds;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import dev.yumi.mc.core.api.ModContainer;
import dev.yumi.mc.core.api.YumiMods;
import dev.yumi.mc.core.api.entrypoint.client.ClientModInitializer;

public class ImmersiveStormsClient implements ClientModInitializer {
    private static boolean isDistantHorizonsLoaded = false;

    @Override
    public void onInitializeClient(ModContainer mod) {
        checkDistantHorizons();
        registerConfig();

        boolean disableSandstormEffects = SandstormConfig.HANDLER.instance().isDetectParticleRain()
                && YumiMods.get().isModLoaded("particlerain");

        if (!disableSandstormEffects) {
            ISClientTickEvents.END_LEVEL.register(new SandstormParticles());
            ISClientTickEvents.END_LEVEL.register(new SandstormSounds());
        } else {
            ImmersiveStorms.LOGGER.info("Particle Rain has been detected, disabling Immersive Storms sandstorm particle and sound effects");
        }

        ISClientTickEvents.END_LEVEL.register(new BiomeWindEffects());

        ClientParticleHelper particleRegistry = ClientParticleHelper.getInstance();
        particleRegistry.registerProvider(ISParticleTypes.DUST_GRAIN, DustGrainParticle.Provider::new);
        particleRegistry.registerProvider(ISParticleTypes.BLACK_RAIN, BlackWaterDropParticle.Provider::new);
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
        isDistantHorizonsLoaded = YumiMods.get().isModLoaded("distanthorizons");
    }
}