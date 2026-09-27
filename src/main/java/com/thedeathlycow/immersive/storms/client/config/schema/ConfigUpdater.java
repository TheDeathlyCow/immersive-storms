package com.thedeathlycow.immersive.storms.client.config.schema;

import java.io.IOException;

public interface ConfigUpdater {
    void run(int originalSchemaVersion) throws IOException;
}