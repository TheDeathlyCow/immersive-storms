package com.thedeathlycow.immersive.storms.network;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.config.section.AdvancedSettings;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record ConfigSyncPacket(AdvancedSettings settings) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ConfigSyncPacket> TYPE = new CustomPacketPayload.Type<>(
            ImmersiveStorms.id("point_wind_spawn")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPacket> STREAM_CODEC = StreamCodec.composite(
            AdvancedSettings.STREAM_CODEC,
            p -> p.settings,
            ConfigSyncPacket::new
    );

    @Override
    @NonNull
    public Type<ConfigSyncPacket> type() {
        return TYPE;
    }
}