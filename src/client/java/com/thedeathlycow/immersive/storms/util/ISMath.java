package com.thedeathlycow.immersive.storms.util;

import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class ISMath {
    public static Vector3f unpackRgb(int rgb) {
        float red = ColorHelper.getRedFloat(rgb);
        float green = ColorHelper.getGreenFloat(rgb);
        float blue = ColorHelper.getBlueFloat(rgb);

        return new Vector3f(red, green, blue);
    }

    public static Vec3d unpackRgb64(int rgb) {
        double red = ColorHelper.getRedFloat(rgb);
        double green = ColorHelper.getGreenFloat(rgb);
        double blue = ColorHelper.getBlueFloat(rgb);

        return new Vec3d(red, green, blue);
    }

    public static int packRgb(Vector3fc rgb) {
        return ColorHelper.getArgb(
                ColorHelper.channelFromFloat(rgb.x()),
                ColorHelper.channelFromFloat(rgb.y()),
                ColorHelper.channelFromFloat(rgb.z())
        );
    }

    public static Vector3fc lerp(float delta, Vector3fc start, Vector3fc end) {
        return new Vector3f(
                MathHelper.lerp(delta, start.x(), end.x()),
                MathHelper.lerp(delta, start.y(), end.y()),
                MathHelper.lerp(delta, start.z(), end.z())
        );
    }

    public static Vec3d lerp(double delta, Vec3d start, Vec3d end) {
        return new Vec3d(
                MathHelper.lerp(delta, start.x, end.x),
                MathHelper.lerp(delta, start.y, end.y),
                MathHelper.lerp(delta, start.z, end.z)
        );
    }

    private ISMath() {

    }
}