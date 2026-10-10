package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;

public final class ISEnvironmentAttributes {
    public static final EnvironmentAttribute<Float> STORM_FOG_END_DISTANCE = register(
            "visual/storm_fog_end_distance",
            EnvironmentAttribute.builder(AttributeTypes.FLOAT)
                    .defaultValue(1.0f)
                    .spatiallyInterpolated()
                    .syncable()
    );

    public static final EnvironmentAttribute<Boolean> HAS_BLACK_RAIN = register(
            "visual/has_black_rain",
            EnvironmentAttribute.builder(AttributeTypes.BOOLEAN)
                    .defaultValue(false)
                    .syncable()
    );

    public static final EnvironmentAttribute<Boolean> HAS_AMBIENT_WIND = register(
            "visual/has_ambient_wind",
            EnvironmentAttribute.builder(AttributeTypes.BOOLEAN)
                    .defaultValue(false)
                    .syncable()
    );

    public static void initialize() {
        ImmersiveStorms.LOGGER.debug("Initialized environment attributes");
    }

    private static <Value> EnvironmentAttribute<Value> register(final String id, final EnvironmentAttribute.Builder<Value> attributeBuilder) {
        EnvironmentAttribute<Value> attribute = attributeBuilder.build();
        Registry.register(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, ImmersiveStorms.id(id), attribute);
        return attribute;
    }

    private ISEnvironmentAttributes() {

    }
}