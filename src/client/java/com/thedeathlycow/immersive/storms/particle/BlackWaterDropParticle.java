package com.thedeathlycow.immersive.storms.particle;

import com.thedeathlycow.immersive.storms.world.BlackRainEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class BlackWaterDropParticle extends WaterDropParticle {
    public BlackWaterDropParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
        super(level, x, y, z, sprite);
        this.rCol = BlackRainEffect.COLOR_FLOAT;
        this.gCol = BlackRainEffect.COLOR_FLOAT;
        this.bCol = BlackRainEffect.COLOR_FLOAT;
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        public BlackWaterDropParticle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x, double y, double z,
                double xSpeed, double ySpeed, double zSpeed,
                RandomSource random
        ) {
            return new BlackWaterDropParticle(level, x, y, z, this.sprite.get(random));
        }
    }
}