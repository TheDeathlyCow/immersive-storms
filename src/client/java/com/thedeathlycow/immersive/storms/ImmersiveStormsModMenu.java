package com.thedeathlycow.immersive.storms;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class ImmersiveStormsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(ImmersiveStormsConfig.class, parent).get();
    }
}