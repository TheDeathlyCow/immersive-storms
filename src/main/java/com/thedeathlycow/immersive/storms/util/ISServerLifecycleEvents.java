package com.thedeathlycow.immersive.storms.util;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import dev.yumi.commons.event.Event;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

public final class ISServerLifecycleEvents {
    public static final Event<Identifier, ServerStarting> SERVER_STARTING = ImmersiveStorms.EVENT_MANAGER.create(
            ServerStarting.class,
            listeners -> (server) -> {
                for (ServerStarting listener : listeners) {
                    listener.onServerStarting(server);
                }
            });

    @FunctionalInterface
    public interface ServerStarting {
        void onServerStarting(MinecraftServer server);
    }

    private ISServerLifecycleEvents() {

    }
}