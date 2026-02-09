package com.thedeathlycow.immersive.storms.config.schema;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JsonCopyHelper {
    public static void copyBooleanProperty(JsonObject src, JsonObject dest, String propertyName) {
        dest.addProperty(propertyName, src.remove(propertyName).getAsBoolean());
    }
    public static void convertIntToFloatMultiplier(JsonObject src, String name, int oldDefaultValue, float base) {
        int savedValue = Math.abs(src.remove(name).getAsInt());

        float multiplier = (float) savedValue / oldDefaultValue;

        src.addProperty(name, multiplier * base);
    }

    public static void rename(JsonObject json, String oldName, String newName) {
        JsonElement prop = json.remove(oldName);
        json.add(newName, prop);
    }

    public static JsonObject read(Path path) throws IOException {
        return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
    }

    private JsonCopyHelper() {

    }
}