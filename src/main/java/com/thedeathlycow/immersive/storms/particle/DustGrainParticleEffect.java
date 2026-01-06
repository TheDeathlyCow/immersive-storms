package com.thedeathlycow.immersive.storms.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedeathlycow.immersive.storms.registry.ISParticleTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector3fc;

public class DustGrainParticleEffect extends AbstractDustParticleEffect {
    public static final MapCodec<DustGrainParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codecs.VECTOR_3F
                                    .fieldOf("color")
                                    .forGetter(DustGrainParticleEffect::getColor),
                            SCALE_CODEC
                                    .fieldOf("scale")
                                    .forGetter(DustGrainParticleEffect::getScale)
                    )
                    .apply(instance, DustGrainParticleEffect::new)
    );

    public static final PacketCodec<RegistryByteBuf, DustGrainParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR_3F,
            DustGrainParticleEffect::getColor,
            PacketCodecs.FLOAT,
            DustGrainParticleEffect::getScale,
            DustGrainParticleEffect::new
    );

    private final Vector3fc color;

    public DustGrainParticleEffect(Vector3fc color, float scale) {
        super(scale);
        this.color = color;
    }

    public DustGrainParticleEffect(int color, float scale) {
        this(ColorHelper.toRgbVector(color), scale);
    }

    @Override
    public ParticleType<DustGrainParticleEffect> getType() {
        return ISParticleTypes.DUST_GRAIN;
    }

    public Vector3fc getColor() {
        return color;
    }
}
