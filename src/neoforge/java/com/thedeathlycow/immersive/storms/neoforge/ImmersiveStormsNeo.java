package com.thedeathlycow.immersive.storms.neoforge;


import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.network.ConfigSyncPacketPayload;
import com.thedeathlycow.immersive.storms.util.ISCommandRegistrationCallback;
import com.thedeathlycow.immersive.storms.util.ISServerLifecycleEvents;
import dev.yumi.mc.core.api.YumiMods;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(ImmersiveStorms.MOD_ID)
public class ImmersiveStormsNeo {
    public ImmersiveStormsNeo(IEventBus modBus) {
        modBus.addListener(ImmersiveStormsNeo::registerPayloadTypes);
        NeoForge.EVENT_BUS.addListener(ImmersiveStormsNeo::onCommandRegistered);
        NeoForge.EVENT_BUS.addListener(ImmersiveStormsNeo::onServerStarting);
        NeoForge.EVENT_BUS.addListener(ImmersiveStormsNeo::onPlayerJoin);

        if (YumiMods.get().isDevelopmentEnvironment()) {
            ImmersiveStorms.LOGGER.info("Thermoo Neoforge @Mod class initialized");
        }

        ConfigSyncPacketPayload.SEND_TO_CLIENTS.register((_, payload) -> {
            PacketDistributor.sendToAllPlayers(payload);
        });
    }

    private static void onCommandRegistered(RegisterCommandsEvent event) {
        ISCommandRegistrationCallback.EVENT.invoker().register(
                event.getDispatcher(),
                event.getBuildContext(),
                event.getCommandSelection()
        );
    }

    private static void onServerStarting(ServerStartingEvent event) {
        ISServerLifecycleEvents.SERVER_STARTING.invoker().onServerStarting(event.getServer());
    }

    private static void registerPayloadTypes(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(ConfigSyncPacketPayload.TYPE, ConfigSyncPacketPayload.STREAM_CODEC);
    }

    private static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(ConfigSyncPacketPayload.createClientboundPacket());
        }
    }
}