package com.thedeathlycow.immersive.storms.mixin;

import com.thedeathlycow.immersive.storms.world.AttributeMapHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public class LevelMixin implements AttributeMapHolder {
}