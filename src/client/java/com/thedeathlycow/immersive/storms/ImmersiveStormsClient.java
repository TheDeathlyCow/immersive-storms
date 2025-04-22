package com.thedeathlycow.immersive.storms;

import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;

public class ImmersiveStormsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
    }

    public static ImmersiveStormsConfig getConfig() {
        return ImmersiveStorms.getConfig();
    }
}