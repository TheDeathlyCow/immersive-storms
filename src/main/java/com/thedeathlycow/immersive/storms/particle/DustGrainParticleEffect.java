package com.thedeathlycow.immersive.storms.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3fc;

public class DustGrainParticleEffect extends ScalableParticleOptionsBase {
    public static final MapCodec<DustGrainParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            ExtraCodecs.VECTOR3F
                                    .fieldOf("color")
                                    .forGetter(DustGrainParticleEffect::getColor),
                            SCALE
                                    .fieldOf("scale")
                                    .forGetter(DustGrainParticleEffect::getScale)
                    )
                    .apply(instance, DustGrainParticleEffect::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DustGrainParticleEffect> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F,
            DustGrainParticleEffect::getColor,
            ByteBufCodecs.FLOAT,
            DustGrainParticleEffect::getScale,
            DustGrainParticleEffect::new
    );

    private final Vector3fc color;

    public DustGrainParticleEffect(Vector3fc color, float scale) {
        super(scale);
        this.color = color;
    }

    public DustGrainParticleEffect(int color, float scale) {
        this(ARGB.vector3fFromRGB24(color), scale);
    }

    @Override
    public ParticleType<DustGrainParticleEffect> getType() {
        return ISParticleTypes.DUST_GRAIN;
    }

    public Vector3fc getColor() {
        return color;
    }
}
