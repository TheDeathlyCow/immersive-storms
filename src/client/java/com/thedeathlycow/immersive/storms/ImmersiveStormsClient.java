package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class ImmersiveStormsClient implements ClientModInitializer {

    private static ConfigHolder<ImmersiveStormsConfig> configHolder = null;

    @Override
    public void onInitializeClient() {
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
}