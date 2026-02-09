package com.thedeathlycow.immersive.storms.config.schema;

import com.google.gson.JsonObject;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.config.section.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.config.section.SandstormConfig;

import java.io.IOException;
import java.nio.file.Files;

public final class SchemaV2 {
    public static void run(int originalSchemaVersion) throws IOException {
        // load old files
        JsonObject sandstormConfig = JsonCopyHelper.read(SandstormConfig.PATH);
        JsonObject generalConfig = JsonCopyHelper.read(ImmersiveStormsConfig.PATH);

        // apply updates
        JsonCopyHelper.copyBooleanProperty(sandstormConfig, generalConfig, "enableSandstormSounds");
        JsonCopyHelper.rename(generalConfig, "enableSandstormSounds", "enableStrongWindSounds");

        // save files
        Files.createDirectories(ImmersiveStorms.getConfigDir());
        Files.writeString(SandstormConfig.PATH, sandstormConfig.toString());
        Files.writeString(ImmersiveStormsConfig.PATH, generalConfig.toString());
    }

    private SchemaV2() {

    }
}