package com.thedeathlycow.immersive.storms.client.config.ui;

import com.thedeathlycow.immersive.storms.config.Translate;
import com.thedeathlycow.immersive.storms.config.section.AdvancedSettings;
import com.thedeathlycow.immersive.storms.config.section.GeneralSettings;
import com.thedeathlycow.immersive.storms.config.section.SandstormSettings;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.inventory.ClickAction;

public class ISConfigScreen {
    private static final String GENERAL_PREFIX = Translate.prefixKey(GeneralSettings.HANDLER);
    private static final String SANDSTORM_PREFIX = Translate.prefixKey(SandstormSettings.HANDLER);
    private static final String ADVANCED_PREFIX = Translate.prefixKey(AdvancedSettings.HANDLER);
    public static final String TITLE = GENERAL_PREFIX + ".title";
    public static final String GENERAL_CATEGORY = GENERAL_PREFIX + ".category.general";
    public static final String SANDSTORM_CATEGORY = SANDSTORM_PREFIX + ".category.sandstorms";
    public static final String ADVANCED_CATEGORY = ADVANCED_PREFIX + ".category.sandstorms";
    public static final String GENERAL_CATEGORY_DESC = GENERAL_PREFIX + ".category.desc";
    public static final String SANDSTORM_CATEGORY_DESC = SANDSTORM_PREFIX + ".category.desc";
    public static final String ADVANCED_CATEGORY_DESC = ADVANCED_PREFIX + ".category.desc";

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
                                                    .setScreen(GeneralSettings.HANDLER
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
                                                    .setScreen(SandstormSettings.HANDLER
                                                            .generateGui()
                                                            .generateScreen(yaclScreen));
                                        }).build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.translatable(ADVANCED_CATEGORY))
                                        .description(
                                                OptionDescription.createBuilder()
                                                        .text(Component.translatable(ADVANCED_CATEGORY_DESC))
                                                        .build()
                                        )
                                        .text(Component.literal(""))
                                        .action((yaclScreen, buttonOption) -> {
                                            Util.getPlatform().openFile(AdvancedSettings.PATH.toFile());
                                        }).build())
                                .build()
                )
                .build()
                .generateScreen(parent);
    }
}