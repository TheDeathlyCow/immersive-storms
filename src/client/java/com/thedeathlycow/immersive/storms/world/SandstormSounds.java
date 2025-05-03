package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public final class SandstormSounds implements ClientTickEvents.EndWorldTick {
    private static final int MAX_SOUND_Y_DIFF = 10;

    private static final int MAX_XZ_OFFSET = 10;

    private int timer = 0;

    @Override
    public void onEndTick(ClientWorld world) {
        if (world.getRainGradient(1f) < 0.7f || world.getTickManager().isFrozen()) {
            return;
        }

        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();
        boolean enabled = config.getSandstorm().isEnableSandstormSounds()
                && config.isEnabled(WeatherEffectType.SANDSTORM);

        if (!enabled) {
            return;
        }

        final MinecraftClient gameClient = MinecraftClient.getInstance();
        final Camera camera = gameClient.gameRenderer.getCamera();
        if (camera == null) {
            return; // no camera for whatever reason
        }

        if (!world.isRaining()) {
            return;
        }

        if (world.random.nextInt(3) >= this.timer) {
            this.timer++;
            return;
        }
        this.timer = 0;

        this.chooseSpotForWindSound(world, camera).ifPresent(
                pos -> {
                    world.playSoundAtBlockCenterClient(
                            pos,
                            ISSoundEvents.WEATHER_SANDSTORM,
                            SoundCategory.WEATHER,
                            0.5f, 1f,
                            false
                    );
                }
        );
    }

    private Optional<BlockPos> chooseSpotForWindSound(ClientWorld world, Camera camera) {
        BlockPos cameraPos = camera.getBlockPos();

        int dx = world.random.nextBetween(-MAX_XZ_OFFSET, MAX_SOUND_Y_DIFF);
        int dy = world.random.nextBetween(-MAX_XZ_OFFSET, MAX_SOUND_Y_DIFF);
        int dz = world.random.nextBetween(-MAX_XZ_OFFSET, MAX_SOUND_Y_DIFF);
        BlockPos soundPos = cameraPos.add(dx, dy, dz);

        if (WeatherEffectsClient.getCurrentType(world, soundPos, true) == WeatherEffectType.SANDSTORM) {
            return Optional.of(soundPos);
        }

        return Optional.empty();
    }
}