package com.thedeathlycow.immersive.storms;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public final class ISBiomeTags {
    public static final TagKey<Biome> HAS_SANDSTORMS = create("has_sandstorms");

    private static TagKey<Biome> create(String id) {
        return TagKey.of(RegistryKeys.BIOME, ImmersiveStorms.id(id));
    }

    private ISBiomeTags() {

    }
}