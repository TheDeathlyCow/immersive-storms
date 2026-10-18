package com.thedeathlycow.immersive.storms.config.schema;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;

import java.io.IOException;
import java.nio.file.Files;

public final class SchemaV3 {
    public static void run(int originalSchemaVersion) throws IOException {
        Files.delete(ImmersiveStorms.getConfigDir().resolve("biomes.json5"));
    }

    private SchemaV3() {

    }
}