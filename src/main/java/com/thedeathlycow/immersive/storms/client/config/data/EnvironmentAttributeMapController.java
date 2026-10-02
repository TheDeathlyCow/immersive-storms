package com.thedeathlycow.immersive.storms.client.config.data;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.attribute.EnvironmentAttributeMap;

import java.lang.reflect.Type;

public class EnvironmentAttributeMapController {

    public static final class GsonAdapter implements JsonDeserializer<EnvironmentAttributeMap>, JsonSerializer<EnvironmentAttributeMap> {
        @Override
        public EnvironmentAttributeMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            DataResult<Pair<EnvironmentAttributeMap, JsonElement>> result = EnvironmentAttributeMap.CODEC.decode(JsonOps.INSTANCE, json);
            return result.getPartialOrThrow(JsonParseException::new).getFirst();
        }

        @Override
        public JsonElement serialize(EnvironmentAttributeMap src, Type typeOfSrc, JsonSerializationContext context) {
            DataResult<JsonElement> result = EnvironmentAttributeMap.CODEC.encodeStart(JsonOps.INSTANCE, src);
            return result.getPartialOrThrow(JsonParseException::new);
        }
    }
}