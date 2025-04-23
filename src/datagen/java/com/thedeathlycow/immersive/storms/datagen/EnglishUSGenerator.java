package com.thedeathlycow.immersive.storms.datagen;

import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.config.NoComment;
import com.thedeathlycow.immersive.storms.config.OptionName;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

public class EnglishUSGenerator extends FabricLanguageProvider {
    private static final String PREFIX = "text.autoconfig.immersive-storms";

    protected EnglishUSGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder builder) {
        builder.add("subtitles.weather.immersive-storms.sandstorm", "Wind blows");

        builder.add(PREFIX + ".title", "Immersive Storms");

        generateConfigOptionTranslations(builder, ImmersiveStormsConfig.class);
    }

    private void generateConfigOptionTranslations(
            TranslationBuilder builder,
            Class<?> configClass
    ) {
        for (Field field : configClass.getDeclaredFields()) {
            String nameKey = configOption(field.getName());

            OptionName nameData = field.getAnnotation(OptionName.class);
            if (nameData != null) {
                builder.add(nameKey, nameData.value());
            } else {
                ImmersiveStormsDataGenerator.LOGGER.error("Option name missing for {}", nameKey);
            }

            Comment commentData = field.getAnnotation(Comment.class);
            String commentKey = tooltip(field.getName());

            if (commentData != null) {
                String comment = commentData.value();
                builder.add(commentKey, comment);
            } else if (field.getAnnotation(NoComment.class) == null) {
                ImmersiveStormsDataGenerator.LOGGER.error("Missing comment for {}", commentKey);
            }
        }
    }

    private static String configOption(String name) {
        return PREFIX + ".option." + name;
    }

    private static String tooltip(String name) {
        return configOption(name) + ".@Tooltip";
    }
}