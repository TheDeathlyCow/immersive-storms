package com.thedeathlycow.immersive.storms.fabric;

import com.thedeathlycow.immersive.storms.util.ISCommandRegistrationCallback;
import dev.yumi.mc.core.api.ModContainer;
import dev.yumi.mc.core.api.entrypoint.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ImmersiveStormsFabric implements ModInitializer {
    @Override
    public void onInitialize(ModContainer modContainer) {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
            ISCommandRegistrationCallback.EVENT.invoker().register(dispatcher, buildContext, selection);
        });
    }
}