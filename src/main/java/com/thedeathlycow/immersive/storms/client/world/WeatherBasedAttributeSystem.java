package com.thedeathlycow.immersive.storms.client.world;

import com.thedeathlycow.immersive.storms.client.config.section.AttributeConfigData;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.GaussianSampler;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public final class WeatherBasedAttributeSystem {
    private @Nullable Level level;
    private @Nullable Vec3 position;
    private @Nullable Biome2WeatherAttributeMap attributeMap;
    private final WeatherBasedAttributeProbe rainProbe = new WeatherBasedAttributeProbe(AttributeConfigData.WeatherAttributeData::rainAttributes);
    private final WeatherBasedAttributeProbe thunderProbe = new WeatherBasedAttributeProbe(AttributeConfigData.WeatherAttributeData::thunderAttributes);

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

        GaussianSampler.sample(
                position.scale(0.25),
                level.getBiomeManager()::getNoiseBiomeAtQuart,
                (weight, biome) -> {
                    biome.unwrapKey().ifPresent(key -> {
                        AttributeConfigData.WeatherAttributeData data = this.attributeMap.getWeatherAttributes(key);
                        this.rainProbe.accumulate(weight, data.rainAttributes());
                        this.thunderProbe.accumulate(weight, data.thunderAttributes());
                    });
                }
        );
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
        private final Function<AttributeConfigData.WeatherAttributeData, EnvironmentAttributeMap> mapData;

        public WeatherBasedAttributeProbe(Function<AttributeConfigData.WeatherAttributeData, EnvironmentAttributeMap> mapData) {
            this.mapData = mapData;
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

        @SuppressWarnings("unchecked")
        public <Value> Value apply(
                final EnvironmentAttribute<Value> attribute,
                final float weatherLevel,
                final float partialTicks,
                final Value baseValue
        ) {
            ValueProbe<Value> valueProbe = (ValueProbe<Value>) this.valueProbes.computeIfAbsent(
                    attribute, attr -> new ValueProbe<>(attr, this.interpolator, this.mapData)
            );
            return attribute.type().stateChangeLerp().apply(weatherLevel, baseValue, valueProbe.get(attribute, weatherLevel));
        }
    }

    public final class ValueProbe<Value> {
        private Value lastValue;
        private @Nullable Value newValue;
        private final SpatialAttributeInterpolator interpolator;
        private final Function<AttributeConfigData.WeatherAttributeData, EnvironmentAttributeMap> mapData;

        public ValueProbe(final EnvironmentAttribute<Value> attribute, final SpatialAttributeInterpolator interpolator, final Function<AttributeConfigData.WeatherAttributeData, EnvironmentAttributeMap> mapData) {
            this.interpolator = interpolator;
            this.mapData = mapData;
            Value value = this.getValueFromPosition(attribute);
            this.lastValue = value;
            this.newValue = value;
        }

        @NonNull
        private Value getValueFromPosition(final EnvironmentAttribute<Value> attribute) {
            if (level == null || position == null || attributeMap == null) {
                return attribute.defaultValue();
            }

            Value defaultValue = level.environmentAttributes().getValue(attribute, position, this.interpolator);

            if (attribute.isSpatiallyInterpolated()) {
                return this.interpolator.applyAttributeLayer(attribute, defaultValue);
            }

            BlockPos quartPos = BlockPos.containing(position.scale(0.25));
            Holder<Biome> biome = level.getBiomeManager().getNoiseBiomeAtQuart(quartPos.getX(), quartPos.getY(), quartPos.getZ());
            AttributeConfigData.WeatherAttributeData weatherData = attributeMap.getWeatherAttributes(biome.unwrapKey().orElseThrow());

            return this.mapData.apply(weatherData).applyModifier(attribute, defaultValue);
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