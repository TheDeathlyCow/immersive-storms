package com.thedeathlycow.immersive.storms.client.config.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import net.minecraft.resources.Identifier;

public record IdentifierController(
        Option<Identifier> option
) implements IStringController<Identifier> {
    @Override
    public String getString() {
        return option.pendingValue().toShortString();
    }

    @Override
    public void setFromString(String value) {
        Identifier id = Identifier.tryParse(value);

        if (id != null) {
            option.requestSet(id);
        } else {
            option.requestSet(option.pendingValue());
        }
    }

    @Override
    public Option<Identifier> option() {
        return this.option;
    }
}
