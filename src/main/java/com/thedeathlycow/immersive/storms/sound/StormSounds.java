package com.thedeathlycow.immersive.storms.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

public record StormSounds(Optional<Holder<SoundEvent>> windSound) {
    public static final StormSounds EMPTY = new StormSounds(Optional.empty());

    public static final Codec<StormSounds> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            SoundEvent.CODEC
                                    .optionalFieldOf("wind_sound")
                                    .forGetter(StormSounds::windSound)
                    )
                    .apply(instance, StormSounds::new)
    );

    public static StormSounds of(Holder<SoundEvent> event) {
        return new StormSounds(Optional.of(event));
    }
}