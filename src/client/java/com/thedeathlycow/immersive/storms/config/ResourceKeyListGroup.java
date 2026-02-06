package com.thedeathlycow.immersive.storms.config;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.ListGroup;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;

public class ResourceKeyListGroup<T> implements ListGroup.ValueFactory<ResourceKey<T>>, ListGroup.ControllerFactory<ResourceKey<T>> {
    private final ResourceKey<Registry<T>> registry;
    private final ResourceKey<T> defaultValue;

    public ResourceKeyListGroup(ResourceKey<Registry<T>> registry, ResourceKey<T> defaultValue) {
        this.registry = registry;
        this.defaultValue = defaultValue;
    }

    @Override
    public ControllerBuilder<ResourceKey<T>> createController(
            ListGroup annotation,
            ConfigField<List<ResourceKey<T>>> field,
            OptionAccess storage,
            Option<ResourceKey<T>> option
    ) {
        return new ResourceKeyController.Builder<>(this.registry, option);
    }

    @Override
    public ResourceKey<T> provideNewValue() {
        return this.defaultValue;
    }

    public static final class BiomeListGroup extends ResourceKeyListGroup<Biome> {
        public BiomeListGroup() {
            super(Registries.BIOME, Biomes.PLAINS);
        }
    }
}