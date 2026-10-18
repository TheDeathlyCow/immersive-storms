package com.thedeathlycow.immersive.storms.fabric;

import com.thedeathlycow.immersive.storms.network.ConfigSyncPacketPayload;
import com.thedeathlycow.immersive.storms.util.ISCommandRegistrationCallback;
import com.thedeathlycow.immersive.storms.util.ISServerLifecycleEvents;
import dev.yumi.mc.core.api.ModContainer;
import dev.yumi.mc.core.api.entrypoint.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

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
                ConfigSyncPacketPayload.TYPE,
                ConfigSyncPacketPayload.STREAM_CODEC
        );

        ServerPlayConnectionEvents.JOIN.register((listener, sender, _) -> {
            if (ServerPlayNetworking.canSend(listener, ConfigSyncPacketPayload.TYPE)) {
                sender.sendPacket(ConfigSyncPacketPayload.createClientboundPacket());
            }
        });

        ConfigSyncPacketPayload.SEND_TO_CLIENTS.register((server, payload) -> {
            for (ServerPlayer player : PlayerLookup.all(server)) {
                if (ServerPlayNetworking.canSend(player, ConfigSyncPacketPayload.TYPE)) {
                    ServerPlayNetworking.send(player, payload);
                }
            }
        });
    }
}