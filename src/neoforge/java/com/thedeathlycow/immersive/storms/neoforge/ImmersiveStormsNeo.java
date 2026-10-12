package com.thedeathlycow.immersive.storms.neoforge;


import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.util.ISCommandRegistrationCallback;
import dev.yumi.mc.core.api.YumiMods;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(ImmersiveStorms.MOD_ID)
public class ImmersiveStormsNeo {
    public ImmersiveStormsNeo(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(ImmersiveStormsNeo::onCommandRegistered);

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
}