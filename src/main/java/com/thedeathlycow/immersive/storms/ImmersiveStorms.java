package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import dev.yumi.commons.event.EventManager;
import dev.yumi.mc.core.api.ModContainer;
import dev.yumi.mc.core.api.YumiMods;
import dev.yumi.mc.core.api.entrypoint.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ImmersiveStorms implements ModInitializer {
    public static final String MOD_NAMESPACE = "immersive-storms";
    public static final String MOD_ID = "immersive_storms";
    public static final EventManager<Identifier> EVENT_MANAGER = new EventManager<>(id("default"), Identifier::parse);
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

    @Override
    public void onInitialize(ModContainer mod) {
        ISParticleTypes.initialize();
        ISSoundEvents.initialize();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_NAMESPACE, path);
    }

    public static Path getConfigDir() {
        return YumiMods.get().getConfigDirectory().resolve(MOD_NAMESPACE);
    }
}