package com.thedeathlycow.immersive.storms.client.config;

import com.thedeathlycow.immersive.storms.client.config.section.BiomeConfig;
import com.thedeathlycow.immersive.storms.client.config.section.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.client.config.section.SandstormConfig;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ISConfigScreen {
    private static final String GENERAL_PREFIX = Translate.prefixKey(ImmersiveStormsConfig.HANDLER);
    private static final String SANDSTORM_PREFIX = Translate.prefixKey(SandstormConfig.HANDLER);
    private static final String BIOMES_PREFIX = Translate.prefixKey(BiomeConfig.HANDLER);
    public static final String TITLE = GENERAL_PREFIX + ".title";
    public static final String GENERAL_CATEGORY = GENERAL_PREFIX + ".category.general";
    public static final String SANDSTORM_CATEGORY = SANDSTORM_PREFIX + ".category.sandstorms";
    public static final String BIOMES_CATEGORY = BIOMES_PREFIX + ".category.biomes";
    public static final String GENERAL_CATEGORY_DESC = GENERAL_PREFIX + ".category.desc";
    public static final String SANDSTORM_CATEGORY_DESC = SANDSTORM_PREFIX + ".category.desc";
    public static final String BIOMES_CATEGORY_DESC = BIOMES_PREFIX + ".category.desc";

    public static Screen generateConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Immersive Storms Test"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Component.translatable(TITLE))
                                .option(ButtonOption.createBuilder()
                                        .name(Component.translatable(GENERAL_CATEGORY))
                                        .description(
                                                OptionDescription.createBuilder()
                                                        .text(Component.translatable(GENERAL_CATEGORY_DESC))
                                                        .build()
                                        )
                                        .text(Component.literal(""))
                                        .action((yaclScreen, buttonOption) -> {
                                            Minecraft.getInstance()
                                                    .setScreen(ImmersiveStormsConfig.HANDLER
                                                            .generateGui()
                                                            .generateScreen(yaclScreen));
                                        }).build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.translatable(SANDSTORM_CATEGORY))
                                        .description(
                                                OptionDescription.createBuilder()
                                                        .text(Component.translatable(SANDSTORM_CATEGORY_DESC))
                                                        .build()
                                        )
                                        .text(Component.literal(""))
                                        .action((yaclScreen, buttonOption) -> {
                                            Minecraft.getInstance()
                                                    .setScreen(SandstormConfig.HANDLER
                                                            .generateGui()
                                                            .generateScreen(yaclScreen));
                                        }).build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.translatable(BIOMES_CATEGORY))
                                        .description(
                                                OptionDescription.createBuilder()
                                                        .text(Component.translatable(BIOMES_CATEGORY_DESC))
                                                        .build()
                                        )
                                        .text(Component.literal(""))
                                        .action((yaclScreen, buttonOption) -> {
                                            Minecraft.getInstance()
                                                    .setScreen(BiomeConfig.HANDLER
                                                            .generateGui()
                                                            .generateScreen(yaclScreen));
                                        }).build())
                                .build()
                )
                .build()
                .generateScreen(parent);
    }
}