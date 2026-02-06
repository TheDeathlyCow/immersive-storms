package com.thedeathlycow.immersive.storms.config;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.ListGroup;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class BiomeConfig {
    static final Path PATH = ImmersiveStorms.getConfigDir().resolve("biomes.json5");

    public static final ConfigClassHandler<BiomeConfig> HANDLER = ConfigClassHandler.createBuilder(BiomeConfig.class)
            .id(ImmersiveStorms.id("biomes"))
            .serializer(
                    config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(PATH)
                            .setJson5(true)
                            .build()
            )
            .build();

    private static final String CATEGORY = "default";

    private static final int VERSION = 1;

    @Translate.Name("Schema version")
    @SerialEntry(comment = "Config version, do not touch! Changing this value may result in unexpected behaviour.")
    int version = VERSION;

    @AutoGen(category = CATEGORY)
    @Translate.Name("Excluded biomes")
    @ListGroup(
            valueFactory = ResourceKeyListGroup.BiomeListGroup.class,
            controllerFactory = ResourceKeyListGroup.BiomeListGroup.class
    )
    @SerialEntry(comment = "Exclude specific biomes from custom weather effects")
    List<ResourceKey<Biome>> excludeBiomes = new ArrayList<>();

    @AutoGen(category = CATEGORY)
    @Translate.Name("Sandstorm biomes")
    @ListGroup(
            valueFactory = ResourceKeyListGroup.BiomeListGroup.class,
            controllerFactory = ResourceKeyListGroup.BiomeListGroup.class
    )
    @SerialEntry(comment = "Add new biomes to be affected by sandstorms.")
    List<ResourceKey<Biome>> sandstormBiomes = new ArrayList<>();

    public boolean isBiomeExcluded(Holder<Biome> biomeHolder) {
        return biomeHolder.unwrapKey()
                .map(key -> this.excludeBiomes.contains(key))
                .orElse(false);
    }

    public static BiomeConfig getConfig() {
        return HANDLER.instance();
    }
}