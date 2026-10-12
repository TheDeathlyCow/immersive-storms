package com.thedeathlycow.immersive.storms.registry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class CommonBiomeTags {
    public static final TagKey<Biome> IS_SNOWY = register("is_snowy");
    public static final TagKey<Biome> IS_ICY = register("is_icy");
    public static final TagKey<Biome> IS_AQUATIC_ICY = register("is_aquatic_icy");
    public static final TagKey<Biome> IS_SPOOKY = register("is_spooky");
    public static final TagKey<Biome> IS_SWAMP = register("is_swamp");
    public static final TagKey<Biome> IS_DESERT = register("is_desert");
    public static final TagKey<Biome> IS_BADLANDS = register("is_badlands");
    public static final TagKey<Biome> IS_SAVANNA = register("is_savanna");
    public static final TagKey<Biome> IS_WINDSWEPT = register("is_windswept");
    public static final TagKey<Biome> IS_MOUNTAIN = register("is_mountain");
    public static final TagKey<Biome> PRIMARY_WOOD_TYPE_PALE_OAK = register("primary_wood_type/pale_oak");
    public static final TagKey<Biome> IS_NETHER = register("is_nether");
    public static final TagKey<Biome> IS_END = register("is_end");


    private static TagKey<Biome> register(String name) {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", name));
    }

    private CommonBiomeTags() {

    }
}