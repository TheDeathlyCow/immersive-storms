package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.config.section.AttributeConfigData;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.GaussianSampler;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public final class WeatherBasedAttributeSystem {
    private @Nullable Level level;
    private @Nullable Vec3 position;
    private final WeatherBasedAttributeProbe rainProbe = new WeatherBasedAttributeProbe();
    private final WeatherBasedAttributeProbe thunderProbe = new WeatherBasedAttributeProbe();

    public void reset() {
        this.level = null;
        this.position = null;
        this.rainProbe.clear();
        this.thunderProbe.clear();
    }

    public void tick(@NonNull final Level level, @NonNull final Vec3 position) {
        this.level = level;
        this.position = position;
        this.rainProbe.tick();
        this.thunderProbe.tick();

        Biome2WeatherAttributeMap attributeMap = ((LevelExtension) this.level).immersive_storms$getBiomeToWeatherAttributeMap();

        GaussianSampler.sample(
                position.scale(0.25),
                level.getBiomeManager()::getNoiseBiomeAtQuart,
                (weight, biome) -> {
                    AttributeConfigData.WeatherAttributeData data = attributeMap.getWeatherAttributes(biome.unwrapKey().orElseThrow());
                    this.rainProbe.accumulate(weight, data.rainAttributes());
                    this.thunderProbe.accumulate(weight, data.thunderAttributes());
                }
        );
    }

    public <Value> Value apply(final EnvironmentAttribute<Value> attribute, final float partialTicks, Value baseValue) {
        Value result = baseValue;

        if (this.level == null) {
            return result;
        }

        final float thunderLevel = level.getThunderLevel(partialTicks);
        final float rainLevel = level.getRainLevel(partialTicks) - thunderLevel;

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

        @SuppressWarnings("unchecked")
        public <Value> Value apply(
                final EnvironmentAttribute<Value> attribute,
                final float weatherLevel,
                final float partialTicks,
                final Value baseValue
        ) {
            ValueProbe<Value> valueProbe = (ValueProbe<Value>) this.valueProbes.computeIfAbsent(
                    attribute, attr -> new ValueProbe<>(attr, this.interpolator)
            );
            return attribute.type().stateChangeLerp().apply(weatherLevel, baseValue, valueProbe.get(attribute, partialTicks));
        }
    }

    public final class ValueProbe<Value> {
        private Value lastValue;
        private @Nullable Value newValue;
        private final SpatialAttributeInterpolator interpolator;

        public ValueProbe(final EnvironmentAttribute<Value> attribute, final SpatialAttributeInterpolator interpolator) {
            this.interpolator = interpolator;
            Value value = this.getValueFromLevel(attribute);
            this.lastValue = value;
            this.newValue = value;
        }

        @NonNull
        private Value getValueFromLevel(final EnvironmentAttribute<Value> attribute) {
            return level != null && position != null
                    ? level.environmentAttributes().getValue(attribute, position, this.interpolator)
                    : attribute.defaultValue();
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
                this.newValue = this.getValueFromLevel(attribute);
            }

            return attribute.type().partialTickLerp().apply(partialTicks, this.lastValue, this.newValue);
        }
    }
}