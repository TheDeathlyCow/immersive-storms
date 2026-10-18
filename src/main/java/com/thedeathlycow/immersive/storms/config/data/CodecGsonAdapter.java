package com.thedeathlycow.immersive.storms.config.data;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import java.lang.reflect.Type;

public record CodecGsonAdapter<T>(
        Codec<T> codec
) implements JsonDeserializer<T>, JsonSerializer<T> {
    public static <T> void registerTypeAdapter(GsonBuilder builder, Type type, Codec<T> codec) {
        builder.registerTypeAdapter(type, new CodecGsonAdapter<>(codec));
    }

    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        DataResult<Pair<T, JsonElement>> result = this.codec.decode(JsonOps.INSTANCE, json);
        return result.getPartialOrThrow(JsonParseException::new).getFirst();
    }

    @Override
    public JsonElement serialize(T src, Type type, JsonSerializationContext jsonSerializationContext) {
        DataResult<JsonElement> result = this.codec.encodeStart(JsonOps.INSTANCE, src);
        return result.getPartialOrThrow(JsonParseException::new);
    }
}