package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class ISBlockTags {
    public static final TagKey<Block> PRODUCES_AMBIENT_SNOWY_WIND_PARTICLE = create("produces_ambient_snowy_wind_particle");
    public static final TagKey<Block> PRODUCES_AMBIENT_SANDY_WIND_PARTICLE = create("produces_ambient_sandy_wind_particle");
    public static final TagKey<Block> PRODUCES_AMBIENT_ROCKY_WIND_PARTICLE = create("produces_ambient_rocky_wind_particle");

    private static TagKey<Block> create(String id) {
        return TagKey.of(RegistryKeys.BLOCK, ImmersiveStorms.id(id));
    }

    private ISBlockTags() {

    }
}