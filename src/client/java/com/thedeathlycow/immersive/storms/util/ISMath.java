package com.thedeathlycow.immersive.storms.util;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class ISMath {
    public static Vector3f unpackRgb(int rgb) {
        float red = ARGB.redFloat(rgb);
        float green = ARGB.greenFloat(rgb);
        float blue = ARGB.blueFloat(rgb);

        return new Vector3f(red, green, blue);
    }

    public static Vec3 unpackRgb64(int rgb) {
        double red = ARGB.redFloat(rgb);
        double green = ARGB.greenFloat(rgb);
        double blue = ARGB.blueFloat(rgb);

        return new Vec3(red, green, blue);
    }

    public static int packRgb(Vector3fc rgb) {
        return ARGB.color(
                ARGB.as8BitChannel(rgb.x()),
                ARGB.as8BitChannel(rgb.y()),
                ARGB.as8BitChannel(rgb.z())
        );
    }

    public static Vector3fc lerp(float delta, Vector3fc start, Vector3fc end) {
        return new Vector3f(
                Mth.lerp(delta, start.x(), end.x()),
                Mth.lerp(delta, start.y(), end.y()),
                Mth.lerp(delta, start.z(), end.z())
        );
    }

    public static Vec3 lerp(double delta, Vec3 start, Vec3 end) {
        return new Vec3(
                Mth.lerp(delta, start.x, end.x),
                Mth.lerp(delta, start.y, end.y),
                Mth.lerp(delta, start.z, end.z)
        );
    }

    private ISMath() {

    }
}