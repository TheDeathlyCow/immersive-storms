package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.sound.StormSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.attribute.AttributeType;

public final class ISAttributeTypes {
    public static final AttributeType<StormSounds> STORM_SOUNDS = register(
            "storm_sounds",
            AttributeType.ofNotInterpolated(StormSounds.CODEC)
    );

    public static void initialize() {
        ImmersiveStorms.LOGGER.debug("Initialized attribute types");
    }

    private static <Value> AttributeType<Value> register(final String name, final AttributeType<Value> type) {
        Registry.register(BuiltInRegistries.ATTRIBUTE_TYPE, ImmersiveStorms.id(name), type);
        return type;
    }

    private ISAttributeTypes() {

    }
}