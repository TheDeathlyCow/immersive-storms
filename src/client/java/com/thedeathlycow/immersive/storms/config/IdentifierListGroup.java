package com.thedeathlycow.immersive.storms.config;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.ListGroup;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;

public class IdentifierListGroup implements ListGroup.ValueFactory<Identifier>, ListGroup.ControllerFactory<Identifier> {
    private final Identifier defaultValue;

    public IdentifierListGroup(Identifier defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public ControllerBuilder<Identifier> createController(ListGroup annotation, ConfigField<List<Identifier>> field, OptionAccess storage, Option<Identifier> option) {
        return () -> new IdentifierController(option);
    }

    @Override
    public Identifier provideNewValue() {
        return this.defaultValue;
    }

    public static final class BiomeIdentifierGroup extends IdentifierListGroup {
        public BiomeIdentifierGroup() {
            super(Biomes.PLAINS.identifier());
        }
    }
}