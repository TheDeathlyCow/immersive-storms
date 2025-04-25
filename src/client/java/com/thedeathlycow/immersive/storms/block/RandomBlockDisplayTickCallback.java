package com.thedeathlycow.immersive.storms.block;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@FunctionalInterface
public interface RandomBlockDisplayTickCallback {
    Event<RandomBlockDisplayTickCallback> EVENT = EventFactory.createArrayBacked(
            RandomBlockDisplayTickCallback.class,
            listeners -> (world, state, pos, random) -> {
                for (RandomBlockDisplayTickCallback listener : listeners) {
                    listener.randomDisplayTick(world, state, pos, random);
                }
            }
    );

    void randomDisplayTick(ClientWorld world, BlockState state, BlockPos pos, Random random);
}