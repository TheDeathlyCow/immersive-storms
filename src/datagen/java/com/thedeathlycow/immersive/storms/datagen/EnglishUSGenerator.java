package com.thedeathlycow.immersive.storms.datagen;

import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.config.NoComment;
import com.thedeathlycow.immersive.storms.config.OptionName;
import com.thedeathlycow.immersive.storms.config.SandstormConfig;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundEvent;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

public class EnglishUSGenerator extends FabricLanguageProvider {
    private static final String BASE_PREFIX = "text.autoconfig.immersive-storms";
    private static final String SANDSTORM_PREFIX = BASE_PREFIX + ".option.sandstorm";

    protected EnglishUSGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder builder) {
        addSubtitle(builder, ISSoundEvents.WEATHER_STRONG_WIND, "Wind blows strongly");

        builder.add(BASE_PREFIX + ".title", "Immersive Storms");

        generateConfigOptionTranslations(BASE_PREFIX + ".option", builder, ImmersiveStormsConfig.class);
        generateConfigOptionTranslations(SANDSTORM_PREFIX, builder, SandstormConfig.class);
    }

    private static void addSubtitle(TranslationBuilder builder, SoundEvent event, String subtitle) {
        builder.add(event.id().toTranslationKey("subtitles"), subtitle);
    }

    private void generateConfigOptionTranslations(
            String prefix,
            TranslationBuilder builder,
            Class<?> configClass
    ) {
        for (Field field : configClass.getDeclaredFields()) {
            String nameKey = configOption(prefix, field.getName());

            OptionName nameData = field.getAnnotation(OptionName.class);
            if (nameData != null) {
                builder.add(nameKey, nameData.value());
            } else {
                throw new IllegalStateException("Option name missing for" + nameKey);
            }

            Comment commentData = field.getAnnotation(Comment.class);
            String commentKey = tooltip(prefix, field.getName());

            if (commentData != null) {
                String comment = commentData.value();
                builder.add(commentKey, comment);
            } else if (field.getAnnotation(NoComment.class) == null) {
                throw new IllegalStateException("Missing comment or @NoComment marker for " + commentKey);
            }
        }
    }

    private static String configOption(String prefix, String name) {
        return prefix + "." + name;
    }

    private static String tooltip(String prefix, String name) {
        return configOption(prefix, name) + ".@Tooltip";
    }
}