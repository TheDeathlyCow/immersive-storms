package com.thedeathlycow.immersive.storms.util;

import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public final class ISMath {
    public static Vector3f unpackRgb(int rgb) {
        float red = ColorHelper.getRedFloat(rgb);
        float green = ColorHelper.getGreenFloat(rgb);
        float blue = ColorHelper.getBlueFloat(rgb);

        return new Vector3f(red, green, blue);
    }

    public static Vec3d lerp(float delta, Vec3d start, Vec3d end) {
        return new Vec3d(
                MathHelper.lerp(delta, start.x, end.x),
                MathHelper.lerp(delta, start.y, end.y),
                MathHelper.lerp(delta, start.z, end.z)
        );
    }

    private ISMath() {

    }
}