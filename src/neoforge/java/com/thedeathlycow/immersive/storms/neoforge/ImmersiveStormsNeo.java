package com.thedeathlycow.immersive.storms.neoforge;


import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.util.ISCommandRegistrationCallback;
import com.thedeathlycow.immersive.storms.util.ISServerLifecycleEvents;
import dev.yumi.mc.core.api.YumiMods;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(ImmersiveStorms.MOD_ID)
public class ImmersiveStormsNeo {
    public ImmersiveStormsNeo(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(ImmersiveStormsNeo::onCommandRegistered);
        NeoForge.EVENT_BUS.addListener(ImmersiveStormsNeo::onServerStarting);

        if (YumiMods.get().isDevelopmentEnvironment()) {
            ImmersiveStorms.LOGGER.info("Thermoo Neoforge @Mod class initialized");
        }
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
}