package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public final class ISBiomeTags {
    public static final TagKey<Biome> HAS_SANDSTORMS = create("has_sandstorms");
    public static final TagKey<Biome> HAS_BLIZZARDS = create("has_blizzards");
    public static final TagKey<Biome> HAS_DENSE_FOG = create("has_dense_fog");

    public static final TagKey<Biome> HAS_SANDY_WIND = create("has_sandy_wind");
    public static final TagKey<Biome> HAS_SNOWY_WIND = create("has_snowy_wind");

    private static TagKey<Biome> create(String id) {
        return TagKey.of(RegistryKeys.BIOME, ImmersiveStorms.id(id));
    }

    private ISBiomeTags() {

    }
}