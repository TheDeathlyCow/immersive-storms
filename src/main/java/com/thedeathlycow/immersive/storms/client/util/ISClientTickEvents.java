package com.thedeathlycow.immersive.storms.client.util;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import dev.yumi.commons.event.Event;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;

public final class ISClientTickEvents {
    public static final Event<Identifier, EndLevel> END_LEVEL = ImmersiveStorms.EVENT_MANAGER.create(
            EndLevel.class,
            listeners -> level -> {
                for (EndLevel listener : listeners) {
                    listener.onEndTick(level);
                }
            }
    );


    @FunctionalInterface
    public interface EndLevel {
        void onEndTick(ClientLevel level);
    }
}