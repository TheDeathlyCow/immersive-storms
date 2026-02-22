package com.thedeathlycow.immersive.storms.datagen;

import com.thedeathlycow.immersive.storms.registry.ISBiomeTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import java.util.concurrent.CompletableFuture;

public class ISBiomeTagGenerator extends FabricTagProvider<Biome> {
    public ISBiomeTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BIOME, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(ISBiomeTags.HAS_SANDSTORMS)
                .addOptionalTag(ConventionalBiomeTags.IS_DESERT)
                .addOptionalTag(ConventionalBiomeTags.IS_BADLANDS)
                .addOptionalTag(ConventionalBiomeTags.IS_SAVANNA);

        builder(ISBiomeTags.HAS_BLIZZARDS)
                .addOptionalTag(ConventionalBiomeTags.IS_SNOWY)
                .addOptionalTag(ConventionalBiomeTags.IS_ICY)
                .addOptionalTag(ConventionalBiomeTags.IS_AQUATIC_ICY);

        builder(ISBiomeTags.HAS_DENSE_FOG)
                .add(Biomes.PALE_GARDEN)
                .addOptionalTag(ConventionalBiomeTags.PRIMARY_WOOD_TYPE_PALE_OAK)
                .addOptionalTag(ConventionalBiomeTags.IS_SWAMP);

        builder(ISBiomeTags.IS_WINDY)
                .addOptionalTag(ConventionalBiomeTags.IS_DESERT)
                .addOptionalTag(ConventionalBiomeTags.IS_BADLANDS)
                .addOptionalTag(ConventionalBiomeTags.IS_MOUNTAIN)
                .addOptionalTag(ConventionalBiomeTags.IS_WINDSWEPT);


        builder(ISBiomeTags.IS_BLACK_RAIN_AFFECTED)
                .add(Biomes.PALE_GARDEN)
                .addOptionalTag(ConventionalBiomeTags.PRIMARY_WOOD_TYPE_PALE_OAK);
    }
}