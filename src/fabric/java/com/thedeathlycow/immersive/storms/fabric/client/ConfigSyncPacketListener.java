package com.thedeathlycow.immersive.storms.fabric.client;

import com.thedeathlycow.immersive.storms.network.ConfigSyncPacket;
import com.thedeathlycow.immersive.storms.util.AttributeMapHolder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ConfigSyncPacketListener implements ClientPlayNetworking.PlayPayloadHandler<ConfigSyncPacket> {
    @Override
    public void receive(ConfigSyncPacket payload, ClientPlayNetworking.Context context) {
        AttributeMapHolder mapHolder = (AttributeMapHolder)context.player().connection;
        mapHolder.immersiveStorms$rebindAttributeMap(payload.settings());
    }
}