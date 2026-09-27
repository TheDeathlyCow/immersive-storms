package com.thedeathlycow.immersive.storms.client.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.ExtraCodecs;

public final class ColorHelper {
    public static final Codec<Integer> CODEC = Codec.withAlternative(
            ExtraCodecs.intRange(0, 0xFFFFFF),
            Codec.STRING
                    .validate(ColorHelper::isValidColor)
                    .xmap(ColorHelper::toRGB, ColorHelper::fromRGB)
    );

    private static DataResult<String> isValidColor(String str) {
        final int requiredLength = "#000000".length();

        if (str.length() != requiredLength) {
            return DataResult.error(() -> "Color string must be " + requiredLength + " characters long");
        }

        if (!str.startsWith("#")) {
            return DataResult.error(() -> "Color string must start with '#'");
        }

        // start at 1 to skip the required leading # character
        for (int i = 1; i < str.length(); i++) {
            char digit = str.charAt(i);
            if (Character.digit(digit, 16) == -1) {
                return DataResult.error(() -> "Found non hex digit in color code: '" + digit + "' (must be [a-fA-F0-9])");
            }
        }

        return DataResult.success(str);
    }

    private static int toRGB(String str) {
        String code = str.substring(1);
        return Integer.parseUnsignedInt(code, 16);
    }

    private static String fromRGB(int rgb) {
        return String.format("#%06x", rgb);
    }

    private ColorHelper() {

    }
}