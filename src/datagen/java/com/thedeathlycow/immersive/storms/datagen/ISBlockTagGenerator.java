package com.thedeathlycow.immersive.storms.datagen;

import com.thedeathlycow.immersive.storms.registry.ISBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ISBlockTagGenerator extends FabricTagProvider<Block> {

    public ISBlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ISBlockTags.PRODUCES_AMBIENT_SNOWY_WIND_PARTICLE)
                .add(Blocks.SNOW);

        getOrCreateTagBuilder(ISBlockTags.PRODUCES_AMBIENT_SANDY_WIND_PARTICLE)
                .addOptionalTag(BlockTags.SAND)
                .addOptionalTag(BlockTags.TERRACOTTA)
                .addOptionalTag(ConventionalBlockTags.SANDS);

        getOrCreateTagBuilder(ISBlockTags.PRODUCES_AMBIENT_ROCKY_WIND_PARTICLE)
                .add(Blocks.GRAVEL)
                .add(Blocks.COARSE_DIRT)
                .add(Blocks.STONE)
                .add(Blocks.GRASS_BLOCK)
                .addOptionalTag(ConventionalBlockTags.STONES)
                .addOptionalTag(ConventionalBlockTags.GRAVELS);

    }
}