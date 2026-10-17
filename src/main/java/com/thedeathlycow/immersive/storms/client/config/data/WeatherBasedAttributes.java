package com.thedeathlycow.immersive.storms.client.config.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.modifier.AttributeModifier;

public record WeatherBasedAttributes(
        EnvironmentAttributeMap clear,
        EnvironmentAttributeMap rain,
        EnvironmentAttributeMap thunder
) {
    public static final WeatherBasedAttributes EMPTY = new WeatherBasedAttributes(
            EnvironmentAttributeMap.EMPTY,
            EnvironmentAttributeMap.EMPTY,
            EnvironmentAttributeMap.EMPTY
    );

    public static final Codec<WeatherBasedAttributes> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    EnvironmentAttributeMap.CODEC
                            .fieldOf("clear")
                            .forGetter(WeatherBasedAttributes::clear),
                    EnvironmentAttributeMap.CODEC
                            .fieldOf("rain")
                            .forGetter(WeatherBasedAttributes::rain),
                    EnvironmentAttributeMap.CODEC
                            .fieldOf("thunder")
                            .forGetter(WeatherBasedAttributes::thunder)
            ).apply(instance, WeatherBasedAttributes::new)
    );

    public static final Codec<WeatherBasedAttributes> NETWORK_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    EnvironmentAttributeMap.NETWORK_CODEC
                            .fieldOf("clear")
                            .forGetter(WeatherBasedAttributes::clear),
                    EnvironmentAttributeMap.NETWORK_CODEC
                            .fieldOf("rain")
                            .forGetter(WeatherBasedAttributes::rain),
                    EnvironmentAttributeMap.NETWORK_CODEC
                            .fieldOf("thunder")
                            .forGetter(WeatherBasedAttributes::thunder)
            ).apply(instance, WeatherBasedAttributes::new)
    );

    public static Builder builder() {
        return new Builder();
    }

    public EnvironmentAttributeMap forCondition(WeatherCondition condition) {
        return switch (condition) {
            case CLEAR -> this.clear;
            case RAINING -> this.rain;
            case THUNDERING -> this.thunder;
        };
    }

    public static final class Builder {
        private final EnvironmentAttributeMap.Builder clearAttributes = EnvironmentAttributeMap.builder();
        private final EnvironmentAttributeMap.Builder rainAttributes = EnvironmentAttributeMap.builder();
        private final EnvironmentAttributeMap.Builder thunderAttributes = EnvironmentAttributeMap.builder();

        private Builder() {
        }

        public Builder putInAllWeather(final WeatherBasedAttributes data) {
            this.clearAttributes.putAll(data.clear());
            this.rainAttributes.putAll(data.rain());
            this.thunderAttributes.putAll(data.thunder());
            return this;
        }

        public <Value> Builder setAllWeather(
                final EnvironmentAttribute<Value> attribute,
                final Value value
        ) {
            this.clearAttributes.set(attribute, value);
            this.rainAttributes.set(attribute, value);
            this.thunderAttributes.set(attribute, value);
            return this;
        }

        public <Value, Parameter> Builder modifyAllWeather(
                final EnvironmentAttribute<Value> attribute,
                final AttributeModifier<Value, Parameter> modifier,
                final Parameter value
        ) {
            this.clearAttributes.modify(attribute, modifier, value);
            this.rainAttributes.modify(attribute, modifier, value);
            this.thunderAttributes.modify(attribute, modifier, value);
            return this;
        }

        public Builder putAll(final EnvironmentAttributeMap attributes, final WeatherCondition condition) {
            this.forCondition(condition).putAll(attributes);
            return this;
        }

        public <Value, Parameter> Builder modify(
                final EnvironmentAttribute<Value> attribute,
                final AttributeModifier<Value, Parameter> modifier,
                final Parameter value,
                final WeatherCondition condition
        ) {
            attribute.type().checkAllowedModifier(modifier);
            this.forCondition(condition).modify(attribute, modifier, value);
            return this;
        }

        public <Value> Builder set(
                final EnvironmentAttribute<Value> attribute,
                final Value value,
                final WeatherCondition condition
        ) {
            this.forCondition(condition).set(attribute, value);
            return this;
        }

        public WeatherBasedAttributes build() {
            return new WeatherBasedAttributes(
                    this.clearAttributes.build(),
                    this.rainAttributes.build(),
                    this.thunderAttributes.build()
            );
        }

        private EnvironmentAttributeMap.Builder forCondition(WeatherCondition condition) {
            return switch (condition) {
                case CLEAR -> this.clearAttributes;
                case RAINING -> this.rainAttributes;
                case THUNDERING -> this.thunderAttributes;
            };
        }
    }

}