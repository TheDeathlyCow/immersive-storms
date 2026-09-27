package com.thedeathlycow.immersive.storms.neoforge.client;

import com.thedeathlycow.immersive.storms.client.config.ISConfigScreen;
import com.thedeathlycow.immersive.storms.client.util.ISClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@Mod(value = ImmersiveStormsNeoClientMod.MOD_ID, dist = Dist.CLIENT)
public class ImmersiveStormsNeoClientMod {
    public static final String MOD_ID = "immersive_storms";

    public ImmersiveStormsNeoClientMod(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(ImmersiveStormsNeoClientMod::registerParticles);
        modBus.addListener(ImmersiveStormsNeoClientMod::clientLevelTick);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (_, parent) -> {
            return ISConfigScreen.generateConfigScreen(parent);
        });
    }

    private static void clientLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide() && event.getLevel() instanceof ClientLevel clientLevel) {
            ISClientTickEvents.END_LEVEL.invoker().onEndTick(clientLevel);
        }
    }

    private static void registerParticles(RegisterParticleProvidersEvent event) {
        for (var pair : ClientParticleHelperImpl.REGISTRY) {
            pair.register(event);
        }
    }
}