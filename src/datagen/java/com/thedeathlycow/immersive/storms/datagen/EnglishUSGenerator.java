package com.thedeathlycow.immersive.storms.datagen;

import com.thedeathlycow.immersive.storms.ImmersiveStormsModMenu;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.config.SandstormConfig;
import com.thedeathlycow.immersive.storms.config.Translate;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

public class EnglishUSGenerator extends FabricLanguageProvider {
    private static final String BASE_PREFIX = "yacl3.config.immersive-storms";
    private static final String SANDSTORM_PREFIX = BASE_PREFIX + ".option.sandstorm";

    protected EnglishUSGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder builder) {
        builder.add(ISSoundEvents.WEATHER_STRONG_WIND, "Wind blows strongly");

        builder.add(ImmersiveStormsModMenu.TITLE, "Immersive Storms Config");
        builder.add(ImmersiveStormsModMenu.GENERAL_CATEGORY, "General Settings");
        builder.add(ImmersiveStormsModMenu.GENERAL_CATEGORY_DESC, "General settings for Immersive Storms");
        builder.add(ImmersiveStormsModMenu.SANDSTORM_CATEGORY, "Sandstorm Settings");
        builder.add(ImmersiveStormsModMenu.SANDSTORM_CATEGORY_DESC, "Specific settings for sandstorms");

        generateConfigOptionTranslations(ImmersiveStormsConfig.HANDLER, builder);
        generateConfigOptionTranslations(SandstormConfig.HANDLER, builder);
    }

    private <T> void generateConfigOptionTranslations(
            ConfigClassHandler<T> handler,
            TranslationBuilder builder
    ) {
        final String prefix = Translate.prefixKey(handler);

        for (Field field : handler.configClass().getDeclaredFields()) {
            SerialEntry entry = field.getAnnotation(SerialEntry.class);
            if (entry == null) {
                continue;
            }

            Translate.Name nameData = field.getAnnotation(Translate.Name.class);
            String nameKey = configOption(prefix, field.getName());

            if (nameData != null) {
                builder.add(nameKey, nameData.value());
            } else {
                throw new IllegalStateException("Option name missing for" + nameKey);
            }

            String comment = entry.comment();
            String commentKey = commentKey(prefix, field.getName());

            if (comment != null && !comment.isEmpty()) {
                builder.add(commentKey, comment);
            } else if (field.getAnnotation(Translate.NoComment.class) == null) {
                throw new IllegalStateException("Missing comment or @NoComment marker for " + commentKey);
            }
        }
    }

    private static String configOption(String prefix, String name) {
        return prefix + "." + name;
    }

    private static String commentKey(String prefix, String name) {
        return configOption(prefix, name) + ".desc";
    }
}