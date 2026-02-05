package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class ISBiomeTags {
    public static final TagKey<Biome> HAS_SANDSTORMS = create("has_sandstorms");
    public static final TagKey<Biome> HAS_BLIZZARDS = create("has_blizzards");
    public static final TagKey<Biome> HAS_DENSE_FOG = create("has_dense_fog");
    public static final TagKey<Biome> IS_WINDY = create("is_windy");

    private static TagKey<Biome> create(String id) {
        return TagKey.create(Registries.BIOME, ImmersiveStorms.id(id));
    }

    private ISBiomeTags() {

    }
}