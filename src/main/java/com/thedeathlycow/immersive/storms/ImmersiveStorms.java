package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImmersiveStorms implements ModInitializer {
    public static final String MOD_ID = "immersive-storms";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ConfigHolder<ImmersiveStormsConfig> configHolder = null;

    @Override
    public void onInitialize() {
        registerConfig();
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

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}