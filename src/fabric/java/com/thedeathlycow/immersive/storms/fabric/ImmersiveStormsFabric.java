package com.thedeathlycow.immersive.storms.fabric;

import com.thedeathlycow.immersive.storms.network.ConfigSyncPacket;
import com.thedeathlycow.immersive.storms.util.ISCommandRegistrationCallback;
import com.thedeathlycow.immersive.storms.util.ISServerLifecycleEvents;
import dev.yumi.mc.core.api.ModContainer;
import dev.yumi.mc.core.api.entrypoint.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ImmersiveStormsFabric implements ModInitializer {
    @Override
    public void onInitialize(ModContainer modContainer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
            ISCommandRegistrationCallback.EVENT.invoker().register(dispatcher, buildContext, selection);
        });
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ISServerLifecycleEvents.SERVER_STARTING.invoker().onServerStarting(server);
        });

        PayloadTypeRegistry.clientboundPlay().register(
                ConfigSyncPacket.TYPE,
                ConfigSyncPacket.STREAM_CODEC
        );
    }
}