package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeTypes;

public final class ISEnvironmentAttributes {
    public static final EnvironmentAttribute<Integer> SANDSTORM_COLOR = register(
            "visual/sandstorm_color",
            EnvironmentAttribute.builder(EnvironmentAttributeTypes.RGB_COLOR)
                    .defaultValue(0)
                    .interpolated()
                    .synced()
    );

    public static void initialize() {
        ImmersiveStorms.LOGGER.debug("Initialized Immersive Storms environment attributes");
    }

    private static <V> EnvironmentAttribute<V> register(String name, EnvironmentAttribute.Builder<V> builder) {
        return Registry.register(Registries.ENVIRONMENTAL_ATTRIBUTE, ImmersiveStorms.id(name), builder.build());
    }

    private ISEnvironmentAttributes() {

    }
}