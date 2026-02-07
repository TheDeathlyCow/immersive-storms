package com.thedeathlycow.immersive.storms.config;

import com.google.gson.*;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import net.minecraft.IdentifierException;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Type;

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

    public static final class GsonAdapter implements JsonDeserializer<Identifier>, JsonSerializer<Identifier> {
        @Override
        public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return Identifier.parse(json.getAsString());
            } catch (IdentifierException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}
