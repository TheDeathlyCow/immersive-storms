package com.thedeathlycow.immersive.storms.util;

import com.mojang.brigadier.CommandDispatcher;
import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import dev.yumi.commons.event.Event;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;

public interface ISCommandRegistrationCallback {
    Event<Identifier, ISCommandRegistrationCallback> EVENT = ImmersiveStorms.EVENT_MANAGER.create(
            ISCommandRegistrationCallback.class,
            listeners -> (dispatcher, buildContext, selection) -> {
                for (ISCommandRegistrationCallback listener : listeners) {
                    listener.register(dispatcher, buildContext, selection);
                }
            });

    void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection);
}
