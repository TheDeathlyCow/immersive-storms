package com.thedeathlycow.immersive.storms.fabric.client;

import com.thedeathlycow.immersive.storms.client.util.ISClientTickEvents;
import com.thedeathlycow.immersive.storms.network.ConfigSyncPacket;
import com.thedeathlycow.immersive.storms.world.AttributeMapHolder;
import dev.yumi.mc.core.api.ModContainer;
import dev.yumi.mc.core.api.entrypoint.client.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ImmersiveStormsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer modContainer) {
        ClientTickEvents.END_LEVEL_TICK.register(level -> {
            ISClientTickEvents.END_LEVEL.invoker().onEndTick(level);
        });

        ClientPlayNetworking.registerGlobalReceiver(
                ConfigSyncPacket.TYPE,
                (payload, context) -> {
                    AttributeMapHolder mapHolder = context.player().connection;
                    mapHolder.immersive_storms$rebindAttributeMap(payload.settings());
                }
        );
    }
}