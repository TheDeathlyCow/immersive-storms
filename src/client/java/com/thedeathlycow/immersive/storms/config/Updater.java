package com.thedeathlycow.immersive.storms.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * This class is responsible for updating the config from the Cloth Config format to the new YACL format.
 */
public final class Updater {
    public static void initialize() {
        Path clothConfigPath = FabricLoader.getInstance().getConfigDir().resolve("immersive-storms.json5");
        if (Files.exists(clothConfigPath)) {
            try {
                updateToYACL(clothConfigPath);
                ImmersiveStorms.LOGGER.info("Immersive Storms config files successfully updated to YACL format");
            } catch (Exception e) {
                ImmersiveStorms.LOGGER.error("Unable to update config file to YACL", e);
            }
        }
    }

    private static void updateToYACL(Path oldConfigPath) throws IOException {
        ImmersiveStorms.LOGGER.info("Attempting to update Immersive Storms config files to YACL");

        String content = Files.readString(oldConfigPath);

        JsonObject generalConfig = JsonParser.parseString(content).getAsJsonObject();
        JsonObject sandstormConfig = generalConfig.remove("sandstorm").getAsJsonObject();

        copyOldConfigObject(generalConfig, ImmersiveStormsConfig.PATH);
        copyOldConfigObject(sandstormConfig, SandstormConfig.PATH);

        Files.delete(oldConfigPath);
    }

    private static void copyOldConfigObject(JsonObject json, Path dest) throws IOException {
        if (!Files.exists(dest)) {
            Files.createDirectories(dest.getParent());
            Files.writeString(dest, json.toString(), StandardOpenOption.CREATE);
        } else {
            ImmersiveStorms.LOGGER.warn("Config file {} already exists, skipping upgrade", dest);
        }
    }

    private Updater() {

    }
}