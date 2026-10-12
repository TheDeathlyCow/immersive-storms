package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.config.data.WeatherBasedAttributes;
import com.thedeathlycow.immersive.storms.client.config.data.WeatherCondition;
import com.thedeathlycow.immersive.storms.client.util.LevelExtension;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public final class WeatherBasedAttributeInterpolator {
    private @Nullable Level level;
    private @Nullable Vec3 position;
    private @Nullable Biome2WeatherAttributeMap attributeMap;
    private final SpatialAttributeInterpolator baseInterpolator;
    private final WeatherBasedAttributeProbe rainProbe = new WeatherBasedAttributeProbe(WeatherCondition.RAINING);
    private final WeatherBasedAttributeProbe thunderProbe = new WeatherBasedAttributeProbe(WeatherCondition.THUNDERING);

    public WeatherBasedAttributeInterpolator(SpatialAttributeInterpolator baseInterpolator) {
        this.baseInterpolator = baseInterpolator;
    }

    public void reset() {
        this.level = null;
        this.position = null;
        this.attributeMap = null;
        this.rainProbe.clear();
        this.thunderProbe.clear();
    }

    public void tick(@NonNull final Level level, @NonNull final Vec3 position) {
        this.level = level;
        this.position = position;
        this.attributeMap = ((LevelExtension) this.level).immersive_storms$getBiomeToWeatherAttributeMap();

        this.rainProbe.tick();
        this.thunderProbe.tick();
    }

    public void sample(double weight, Holder<Biome> biome) {
        if (this.attributeMap == null) {
            return;
        }

        WeatherBasedAttributes data = this.attributeMap.getWeatherAttributes(biome.value());
        this.rainProbe.accumulate(weight, data.rain());
        this.thunderProbe.accumulate(weight, data.thunder());
    }

    @NotNull
    public EnvironmentAttributeMap getClearWeatherMap(Biome biome, EnvironmentAttributeMap baseAttributes) {
        if (this.attributeMap == null) {
            return baseAttributes;
        }

        return this.attributeMap.getClearWeatherMap(biome, baseAttributes);
    }

    public <Value> Value apply(final EnvironmentAttribute<Value> attribute, final float partialTicks, Value baseValue) {
        Value result = baseValue;

        if (this.level == null) {
            return result;
        }

        final float thunderLevel = level.getThunderLevel(partialTicks);
        final float rainLevel = level.getRainLevel(partialTicks);

        if (rainLevel > 0f) {
            result = this.rainProbe.apply(attribute, rainLevel, partialTicks, result);
        }

        if (thunderLevel > 0f) {
            result = this.thunderProbe.apply(attribute, thunderLevel, partialTicks, result);
        }

        return result;
    }

    public final class WeatherBasedAttributeProbe {
        private final SpatialAttributeInterpolator interpolator = new SpatialAttributeInterpolator();
        private final Map<EnvironmentAttribute<?>, ValueProbe<?>> valueProbes = new Reference2ObjectOpenHashMap<>();
        private final WeatherCondition condition;

        public WeatherBasedAttributeProbe(WeatherCondition condition) {
            this.condition = condition;
        }

        public void tick() {
            this.valueProbes.values().removeIf(ValueProbe::tick);
            this.interpolator.clear();
        }

        public void clear() {
            this.interpolator.clear();
            this.valueProbes.clear();
        }

        public void accumulate(final double weight, final EnvironmentAttributeMap attributes) {
            this.interpolator.accumulate(weight, attributes);
        }

        public <Value> Value apply(
                final EnvironmentAttribute<Value> attribute,
                final float weatherLevel,
                final float partialTicks,
                final Value baseValue
        ) {
            ValueProbe<Value> valueProbe = this.getOrCreate(attribute);
            return attribute.type().stateChangeLerp().apply(weatherLevel, baseValue, valueProbe.get(attribute, partialTicks));
        }

        @SuppressWarnings("unchecked")
        private <Value> ValueProbe<Value> getOrCreate(final EnvironmentAttribute<Value> attribute) {
            return (ValueProbe<Value>) this.valueProbes.computeIfAbsent(
                    attribute, attr -> new ValueProbe<>(attr, this.interpolator, this.condition)
            );
        }

        @SuppressWarnings("unchecked")
        @Nullable
        private <Value> ValueProbe<Value> getOrNull(final EnvironmentAttribute<Value> attribute) {
            return (ValueProbe<Value>) this.valueProbes.get(attribute);
        }
    }

    public final class ValueProbe<Value> {
        private Value lastValue;
        private @Nullable Value newValue;
        private final SpatialAttributeInterpolator interpolator;
        private final WeatherCondition condition;

        public ValueProbe(final EnvironmentAttribute<Value> attribute, final SpatialAttributeInterpolator interpolator, final WeatherCondition condition) {
            this.interpolator = interpolator;
            this.condition = condition;
            Value value = this.getValueFromPosition(attribute);
            this.lastValue = value;
            this.newValue = value;
        }

        @NonNull
        private Value getValueFromPosition(final EnvironmentAttribute<Value> attribute) {
            if (level == null || position == null || attributeMap == null) {
                return attribute.defaultValue();
            }

            Value defaultValue = level.environmentAttributes().getValue(attribute, position, baseInterpolator);

            if (attribute.isSpatiallyInterpolated()) {
                return this.interpolator.applyAttributeLayer(attribute, defaultValue);
            }

            Holder<Biome> biome = level.getBiomeManager().getNoiseBiomeAtPosition(position.x(), position.y(), position.z());
            WeatherBasedAttributes weatherData = attributeMap.getWeatherAttributes(biome.value());

            return weatherData.forCondition(this.condition).applyModifier(attribute, defaultValue);
        }

        public boolean tick() {
            if (this.newValue == null) {
                return true;
            }

            this.lastValue = this.newValue;
            this.newValue = null;
            return false;
        }

        public Value get(final EnvironmentAttribute<Value> attribute, final float partialTicks) {
            if (this.newValue == null) {
                this.newValue = this.getValueFromPosition(attribute);
            }

            return attribute.type().partialTickLerp().apply(partialTicks, this.lastValue, this.newValue);
        }
    }
}