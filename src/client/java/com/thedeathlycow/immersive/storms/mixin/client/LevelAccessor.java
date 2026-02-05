package com.thedeathlycow.immersive.storms.mixin.client;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Level.class)
public interface LevelAccessor {
    @Invoker("canHaveWeather")
    boolean invokeCanHaveWeather();
}
