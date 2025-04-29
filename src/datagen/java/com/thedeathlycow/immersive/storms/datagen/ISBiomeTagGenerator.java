package com.thedeathlycow.immersive.storms.datagen;

import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.concurrent.CompletableFuture;

public class ISBiomeTagGenerator extends FabricTagProvider<Biome> {
    public ISBiomeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BIOME, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ISBiomeTags.HAS_SANDSTORMS)
                .addOptionalTag(ConventionalBiomeTags.IS_DESERT)
                .addOptionalTag(ConventionalBiomeTags.IS_BADLANDS)
                .addOptionalTag(ConventionalBiomeTags.IS_SAVANNA);

        getOrCreateTagBuilder(ISBiomeTags.HAS_BLIZZARDS)
                .addOptionalTag(ConventionalBiomeTags.IS_SNOWY)
                .addOptionalTag(ConventionalBiomeTags.IS_ICY);

        getOrCreateTagBuilder(ISBiomeTags.HAS_DENSE_FOG)
                .add(BiomeKeys.PALE_GARDEN);

        getOrCreateTagBuilder(ISBiomeTags.IS_WINDY)
                .addOptionalTag(ISBiomeTags.HAS_SANDSTORMS)
                .addOptionalTag(ISBiomeTags.HAS_BLIZZARDS)
                .addOptionalTag(ConventionalBiomeTags.IS_MOUNTAIN)
                .addOptionalTag(ConventionalBiomeTags.IS_WINDSWEPT);
    }
}