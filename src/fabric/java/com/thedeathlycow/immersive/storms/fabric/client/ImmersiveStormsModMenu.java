package com.thedeathlycow.immersive.storms.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.thedeathlycow.immersive.storms.client.config.ISConfigScreen;

public class ImmersiveStormsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ISConfigScreen::generateConfigScreen;
    }
}