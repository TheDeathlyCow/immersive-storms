package com.thedeathlycow.immersive.storms;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.config.SandstormConfig;
import com.thedeathlycow.immersive.storms.config.Translate;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ImmersiveStormsModMenu implements ModMenuApi {
    private static final String GENERAL_PREFIX = Translate.prefixKey(ImmersiveStormsConfig.HANDLER);
    private static final String SANDSTORM_PREFIX = Translate.prefixKey(SandstormConfig.HANDLER);
    public static final String TITLE = GENERAL_PREFIX + ".title";
    public static final String GENERAL_CATEGORY = GENERAL_PREFIX + ".category.general";
    public static final String SANDSTORM_CATEGORY = SANDSTORM_PREFIX + ".category.sandstorms";
    public static final String GENERAL_CATEGORY_DESC = GENERAL_PREFIX + ".category.desc";
    public static final String SANDSTORM_CATEGORY_DESC = SANDSTORM_PREFIX + ".category.desc";

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Immersive Storms Test"))
                .category(
                        ConfigCategory.createBuilder()
                                .name(Text.translatable(TITLE))
                                .option(ButtonOption.createBuilder()
                                        .name(Text.translatable(GENERAL_CATEGORY))
                                        .description(
                                                OptionDescription.createBuilder()
                                                        .text(Text.translatable(GENERAL_CATEGORY_DESC))
                                                        .build()
                                        )
                                        .text(Text.literal(""))
                                        .action((yaclScreen, buttonOption) -> {
                                            MinecraftClient.getInstance()
                                                    .setScreen(ImmersiveStormsConfig.HANDLER
                                                            .generateGui()
                                                            .generateScreen(yaclScreen));
                                        }).build())
                                .option(ButtonOption.createBuilder()
                                        .name(Text.translatable(SANDSTORM_CATEGORY))
                                        .description(
                                                OptionDescription.createBuilder()
                                                        .text(Text.translatable(SANDSTORM_CATEGORY_DESC))
                                                        .build()
                                        )
                                        .text(Text.literal(""))
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