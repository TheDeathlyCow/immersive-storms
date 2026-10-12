package com.thedeathlycow.immersive.storms.registry;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import com.thedeathlycow.immersive.storms.client.world.WeatherArchetype;
import com.thedeathlycow.immersive.storms.sound.StormSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
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

    public static final EnvironmentAttribute<Boolean> HAS_SANDSTORM_DUST = register(
            "visual/has_sandstorm_dust",
            EnvironmentAttribute.builder(AttributeTypes.BOOLEAN)
                    .defaultValue(false)
                    .syncable()
    );

    public static final EnvironmentAttribute<StormSounds> STORM_SOUNDS = register(
            "audio/storm_sounds",
            EnvironmentAttribute.builder(ISAttributeTypes.STORM_SOUNDS)
                    .defaultValue(StormSounds.EMPTY)
                    .syncable()
    );

    public static final EnvironmentAttribute<WeatherArchetype> WEATHER_ARCHETYPE = register(
            "gameplay/weather_archetype",
            EnvironmentAttribute.builder(ISAttributeTypes.WEATHER_ARCHETYPE)
                    .defaultValue(WeatherArchetype.NORMAL)
                    .syncable()
    );

    public static void initialize() {
        ImmersiveStorms.LOGGER.debug("Initialized environment weatherAttributes");
    }

    private static <Value> EnvironmentAttribute<Value> register(final String id, final EnvironmentAttribute.Builder<Value> attributeBuilder) {
        EnvironmentAttribute<Value> attribute = attributeBuilder.build();
        Registry.register(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, ImmersiveStorms.id(id), attribute);
        return attribute;
    }

    private ISEnvironmentAttributes() {

    }
}