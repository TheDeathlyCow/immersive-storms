package com.thedeathlycow.immersive.storms.compat;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/// Biome keys from Nature's Spirit used for compatibility.
public final class NSBiomes {
    public static final ResourceKey<Biome> DRYLANDS = key("drylands");
    public static final ResourceKey<Biome> WOODED_DRYLANDS = key("wooded_drylands");

    private static ResourceKey<Biome> key(String path) {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("natures_spirit", path));
    }

    private NSBiomes() {

    }
}