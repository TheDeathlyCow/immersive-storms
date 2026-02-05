package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.util.ISMath;
import net.minecraft.world.attribute.GaussianSampler;
import org.joml.Vector3f;
import org.joml.Vector3fc;

class WeightedVector3fAccumulator implements GaussianSampler.Accumulator<Vector3fc> {
    private final Vector3f sumVector = new Vector3f();
    private double totalWeight;

    @Override
    public void accumulate(double weight, Vector3fc value) {
        var weightedValue = new Vector3f(
                (float) (weight * value.x()),
                (float) (weight * value.y()),
                (float) (weight * value.z())
        );

        sumVector.add(weightedValue);

        totalWeight += weight;
    }

    public Vector3f getAverageVector() {
        return this.sumVector.mul((float) (1.0 / totalWeight), new Vector3f());
    }

    public int getPackedColor() {
        return ISMath.packRgb(this.getAverageVector());
    }
}