package com.thedeathlycow.immersive.storms.mixin.client;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.AmbientDesertBlockSounds;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AmbientDesertBlockSounds.class)
public interface AmbientDesertBlockSoundsAccessor {
    @Invoker("shouldPlayWindSoundIn")
    @Contract(pure = true)
    static boolean invokeShouldPlayWindSoundIn(RegistryEntry<Biome> biome) {
        throw new UnsupportedOperationException("Accessor mixin not properly registered");
    }
}
