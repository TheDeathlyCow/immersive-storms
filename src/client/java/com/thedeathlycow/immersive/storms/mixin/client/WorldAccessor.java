package com.thedeathlycow.immersive.storms.mixin.client;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface WorldAccessor {
    @Invoker("canHaveWeather")
    boolean invokeCanHaveWeather();
}
