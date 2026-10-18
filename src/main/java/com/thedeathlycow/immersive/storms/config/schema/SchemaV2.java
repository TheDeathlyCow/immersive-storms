package com.thedeathlycow.immersive.storms.config.schema;

import com.google.gson.JsonObject;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.config.section.GeneralSettings;
import com.thedeathlycow.immersive.storms.config.section.SandstormSettings;

import java.io.IOException;
import java.nio.file.Files;

public final class SchemaV2 {
    public static void run(int originalSchemaVersion) throws IOException {
        // load old files
        JsonObject sandstormConfig = JsonCopyHelper.read(SandstormSettings.PATH);
        JsonObject generalConfig = JsonCopyHelper.read(GeneralSettings.PATH);

        // apply updates
        JsonCopyHelper.copyBooleanProperty(sandstormConfig, generalConfig, "enableSandstormSounds");
        JsonCopyHelper.rename(generalConfig, "enableSandstormSounds", "enableStrongWindSounds");

        // save files
        Files.createDirectories(ImmersiveStorms.getConfigDir());
        Files.writeString(SandstormSettings.PATH, sandstormConfig.toString());
        Files.writeString(GeneralSettings.PATH, generalConfig.toString());
    }

    private SchemaV2() {

    }
}