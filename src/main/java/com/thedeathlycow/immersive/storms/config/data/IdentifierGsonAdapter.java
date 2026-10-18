package com.thedeathlycow.immersive.storms.config.data;

import com.google.gson.*;
import net.minecraft.IdentifierException;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Type;

public final class IdentifierGsonAdapter implements JsonDeserializer<Identifier>, JsonSerializer<Identifier> {
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