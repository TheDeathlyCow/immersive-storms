package com.thedeathlycow.immersive.storms.particle;

import com.thedeathlycow.immersive.storms.world.PaleRainEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class PaleWaterDropParticle extends WaterDropParticle {
    public PaleWaterDropParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        super(level, x, y, z, sprite);
        this.rCol = PaleRainEffect.COLOR_FLOAT;
        this.gCol = PaleRainEffect.COLOR_FLOAT;
        this.bCol = PaleRainEffect.COLOR_FLOAT;
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public PaleWaterDropParticle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed,
                RandomSource random
        ) {
            return new PaleWaterDropParticle(level, x, y, z, this.sprite.get(random));
        }
    }
}