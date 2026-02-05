package com.thedeathlycow.immersive.storms.world;

import net.minecraft.world.attribute.GaussianSampler;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class WeightedVector2fAccumulator implements GaussianSampler.Accumulator<Vector2fc> {
    private final Vector2f sumVector = new Vector2f();
    private double totalWeight;

    @Override
    public void accumulate(double weight, Vector2fc value) {
        var weightedValue = new Vector2f(
                (float) (weight * value.x()),
                (float) (weight * value.y())
        );

        sumVector.add(weightedValue);

        totalWeight += weight;
    }

    public Vector2fc getAverageVector() {
        return this.sumVector.mul((float) (1.0 / totalWeight), new Vector2f());
    }
}