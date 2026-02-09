package com.thedeathlycow.immersive.storms.config.section;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.config.IdentifierController;
import com.thedeathlycow.immersive.storms.config.IdentifierListGroup;
import com.thedeathlycow.immersive.storms.config.Translate;
import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.ListGroup;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BiomeConfig {
    public static final Path PATH = ImmersiveStorms.getConfigDir().resolve("biomes.json5");

    public static final ConfigClassHandler<BiomeConfig> HANDLER = ConfigClassHandler.createBuilder(BiomeConfig.class)
            .id(ImmersiveStorms.id("biomes"))
            .serializer(
                    config -> GsonConfigSerializerBuilder.create(config)
                            .appendGsonBuilder(gsonBuilder -> {
                                gsonBuilder.registerTypeHierarchyAdapter(
                                        Identifier.class,
                                        new IdentifierController.GsonAdapter()
                                );

                                return gsonBuilder;
                            })
                            .setPath(PATH)
                            .setJson5(true)
                            .build()
            )
            .build();

    private static final String CATEGORY = "biomes";

    @AutoGen(category = CATEGORY)
    @Translate.Name("Excluded biomes")
    @ListGroup(
            valueFactory = IdentifierListGroup.BiomeIdentifierGroup.class,
            controllerFactory = IdentifierListGroup.BiomeIdentifierGroup.class
    )
    @SerialEntry(comment = "Exclude specific biome IDs from custom weather effects")
    List<Identifier> excludeBiomes = new ArrayList<>();

    @AutoGen(category = CATEGORY)
    @Translate.Name("Sandstorm biomes")
    @ListGroup(
            valueFactory = IdentifierListGroup.BiomeIdentifierGroup.class,
            controllerFactory = IdentifierListGroup.BiomeIdentifierGroup.class
    )
    @SerialEntry(comment = "Add new biome IDs to be affected by sandstorms. Particles and sounds will not work in biomes where it is raining or snowing.")
    List<Identifier> sandstormBiomes = new ArrayList<>();

    @AutoGen(category = CATEGORY)
    @Translate.Name("Blizzard biomes")
    @ListGroup(
            valueFactory = IdentifierListGroup.BiomeIdentifierGroup.class,
            controllerFactory = IdentifierListGroup.BiomeIdentifierGroup.class
    )
    @SerialEntry(comment = "Add new biomes IDs to be affected by blizzards.")
    List<Identifier> blizzardBiomes = new ArrayList<>();

    @AutoGen(category = CATEGORY)
    @Translate.Name("Dense fog biomes")
    @ListGroup(
            valueFactory = IdentifierListGroup.BiomeIdentifierGroup.class,
            controllerFactory = IdentifierListGroup.BiomeIdentifierGroup.class
    )
    @SerialEntry(comment = "Add new biomes IDs to be affected by dense fog.")
    List<Identifier> denseFogBiomes = new ArrayList<>();

    @AutoGen(category = CATEGORY)
    @Translate.Name("Windy biomes")
    @ListGroup(
            valueFactory = IdentifierListGroup.BiomeIdentifierGroup.class,
            controllerFactory = IdentifierListGroup.BiomeIdentifierGroup.class
    )
    @SerialEntry(comment = "Add new biomes IDs to be affected by wind particles.")
    List<Identifier> windyBiomes = new ArrayList<>();

    public boolean isBiomeExcluded(Holder<Biome> biomeHolder) {
        return biomeHolder.unwrapKey()
                .map(key -> this.excludeBiomes.contains(key.identifier()))
                .orElse(false);
    }

    public boolean isIncluded(WeatherEffectType type, Holder<Biome> biomeHolder) {
        List<Identifier> inclusion = switch (type) {
            case SANDSTORM -> this.sandstormBiomes;
            case BLIZZARD -> this.blizzardBiomes;
            case DENSE_FOG -> this.denseFogBiomes;
            default -> Collections.emptyList();
        };

        return biomeHolder.unwrapKey()
                .map(key -> inclusion.contains(key.identifier()))
                .orElse(false);
    }

    public boolean isWindy(Holder<Biome> biomeHolder) {
        return ClientTags.isInWithLocalFallback(ISBiomeTags.IS_WINDY, biomeHolder)
                || biomeHolder.unwrapKey().map(key -> this.windyBiomes.contains(key.identifier())).orElse(false);
    }

    public static BiomeConfig getConfig() {
        return HANDLER.instance();
    }
}