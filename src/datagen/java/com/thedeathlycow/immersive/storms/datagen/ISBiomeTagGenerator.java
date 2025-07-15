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
        builder(ISBiomeTags.HAS_SANDSTORMS)
                .addOptionalTag(ConventionalBiomeTags.IS_DESERT)
                .addOptionalTag(ConventionalBiomeTags.IS_BADLANDS)
                .addOptionalTag(ConventionalBiomeTags.IS_SAVANNA);

        builder(ISBiomeTags.HAS_BLIZZARDS)
                .addOptionalTag(ConventionalBiomeTags.IS_SNOWY)
                .addOptionalTag(ConventionalBiomeTags.IS_ICY)
                .addOptionalTag(ConventionalBiomeTags.IS_AQUATIC_ICY);

        builder(ISBiomeTags.HAS_DENSE_FOG)
                .add(BiomeKeys.PALE_GARDEN)
                .addOptionalTag(ConventionalBiomeTags.IS_SWAMP);

        builder(ISBiomeTags.IS_WINDY)
                .addOptionalTag(ConventionalBiomeTags.IS_DESERT)
                .addOptionalTag(ConventionalBiomeTags.IS_BADLANDS)
                .addOptionalTag(ConventionalBiomeTags.IS_MOUNTAIN)
                .addOptionalTag(ConventionalBiomeTags.IS_WINDSWEPT);
    }
}