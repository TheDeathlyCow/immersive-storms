package com.thedeathlycow.immersive.storms.config;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public final class ResourceKeyController<T> implements IStringController<ResourceKey<T>> {
    private final ResourceKey<Registry<T>> registry;
    private final Option<ResourceKey<T>> option;

    public ResourceKeyController(ResourceKey<Registry<T>> registry, Option<ResourceKey<T>> option) {
        this.registry = registry;
        this.option = option;
    }

    @Override
    public String getString() {
        return this.option.pendingValue().identifier().toString();
    }

    @Override
    public void setFromString(String value) {
        Identifier id = Identifier.tryParse(value);

        if (id != null) {
            option.requestSet(ResourceKey.create(this.registry, id));
        } else {
            option.requestSet(option.pendingValue());
        }
    }

    @Override
    public Option<ResourceKey<T>> option() {
        return this.option;
    }

    public static final class Builder<T> implements ControllerBuilder<ResourceKey<T>> {
        private final ResourceKey<Registry<T>> registry;
        private final Option<ResourceKey<T>> option;

        public Builder(ResourceKey<Registry<T>> registry, Option<ResourceKey<T>> option) {
            this.registry = registry;
            this.option = option;
        }

        @Override
        public Controller<ResourceKey<T>> build() {
            return new ResourceKeyController<>(this.registry, this.option);
        }
    }
}