package com.thedeathlycow.immersive.storms.config.schema;

import java.io.IOException;

public interface ConfigUpdater {
    void run(int originalSchemaVersion) throws IOException;
}