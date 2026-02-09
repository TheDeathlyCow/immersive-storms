package com.thedeathlycow.immersive.storms.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.config.schema.ConfigUpdater;
import com.thedeathlycow.immersive.storms.config.schema.SchemaV2;
import com.thedeathlycow.immersive.storms.config.section.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.config.section.SandstormConfig;
import com.thedeathlycow.immersive.storms.config.section.SchemaConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;

/**
 * This class is responsible for updating the config from the Cloth Config format to the new YACL format.
 */
public final class Updater {
    private static final Supplier<Int2ObjectMap<ConfigUpdater>> SCHEMAS = () -> {
        Int2ObjectMap<ConfigUpdater> map = new Int2ObjectArrayMap<>();
        map.put(2, SchemaV2::run);
        return map;
    };

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

        if (!Files.exists(SchemaConfig.PATH) && Files.exists(ImmersiveStormsConfig.PATH)) {
            SchemaConfig.HANDLER.load();
            SchemaConfig.HANDLER.instance().setSchemaVersion(1);
            SchemaConfig.HANDLER.save();
        }

        SchemaConfig.HANDLER.load();
        SchemaConfig schemaConfig = SchemaConfig.HANDLER.instance();

        final int latestSchemaVersion = SchemaConfig.CONFIG_VERSION;
        final int currentSchemaVersion = schemaConfig.getSchemaVersion();

        boolean continueUpgrade;

        if (currentSchemaVersion < latestSchemaVersion) {
            ImmersiveStorms.LOGGER.info("Immersive Storms config is out of date! Config files will be automatically upgraded.");
            continueUpgrade = true;
        } else if (currentSchemaVersion > latestSchemaVersion) {
            ImmersiveStorms.LOGGER.error(
                    "Current Immersive Storms config schema version {} is greater than the latest supported by " +
                            "this version ({}). This may result in unexpected changes to the config files, are you " +
                            "sure you're using the right mod version?",
                    currentSchemaVersion,
                    latestSchemaVersion
            );
            continueUpgrade = false;
        } else {
            ImmersiveStorms.LOGGER.info("Immersive Storms config is up to date!");
            continueUpgrade = false;
        }

        if (!continueUpgrade) {
            return;
        }

        Int2ObjectMap<ConfigUpdater> schemas = SCHEMAS.get();

        for (int step = currentSchemaVersion + 1; step <= latestSchemaVersion; step++) {
            ConfigUpdater updater = schemas.get(step);

            if (updater != null) {
                try {
                    updater.run(currentSchemaVersion);
                } catch (IOException e) {
                    ImmersiveStorms.LOGGER.warn(
                            "Unable to upgrade config file from schema version {} to {}, due to IO error. Aborting upgrade.",
                            step - 1,
                            step,
                            e
                    );
                    break;
                }
            }

            schemaConfig.setSchemaVersion(step);
        }

        SchemaConfig.HANDLER.save();
        ImmersiveStorms.LOGGER.info(
                "Immersive Storms config successfully updated from schema version {} to {}.",
                currentSchemaVersion,
                latestSchemaVersion
        );
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