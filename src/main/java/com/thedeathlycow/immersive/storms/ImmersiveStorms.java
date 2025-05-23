package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImmersiveStorms implements ModInitializer {
    public static final String MOD_ID = "immersive-storms";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ISParticleTypes.initialize();
        ISSoundEvents.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}