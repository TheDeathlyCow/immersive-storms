package com.thedeathlycow.immersive.storms.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.thedeathlycow.immersive.storms.config.ui.ISConfigScreen;

public class ImmersiveStormsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ISConfigScreen::generateConfigScreen;
    }
}