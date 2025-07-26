package com.thedeathlycow.immersive.storms;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.config.SandstormConfig;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ImmersiveStormsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Immersive Storms Test"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Text.literal("Test"))
                                .option(ButtonOption.createBuilder()
                                        .name(Text.literal("General Options Test"))
                                        .action((yaclScreen, buttonOption) -> {
                                            MinecraftClient.getInstance()
                                                    .setScreen(ImmersiveStormsConfig.HANDLER
                                                            .generateGui()
                                                            .generateScreen(yaclScreen));
                                        }).build())
                                .option(ButtonOption.createBuilder()
                                        .name(Text.literal("Sandstorm Options Test"))
                                        .action((yaclScreen, buttonOption) -> {
                                            MinecraftClient.getInstance()
                                                    .setScreen(SandstormConfig.HANDLER
                                                            .generateGui()
                                                            .generateScreen(yaclScreen));
                                        }).build())
                                .build()
                )
                .build()
                .generateScreen(parent);
    }
}