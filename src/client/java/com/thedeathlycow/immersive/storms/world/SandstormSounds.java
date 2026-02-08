package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import java.util.Optional;

public final class SandstormSounds implements ClientTickEvents.EndWorldTick {
    private static final int MAX_SOUND_Y_DIFF = 10;

    private static final int MAX_XZ_OFFSET = 10;

    private int timer = 0;

    @Override
    public void onEndTick(ClientLevel world) {
        if (world.getRainLevel(1f) < 0.7f || world.tickRateManager().isFrozen()) {
            return;
        }

        ImmersiveStormsConfig config = ImmersiveStormsClient.getConfig();
        boolean enabled = config.getSandstorm().isEnableSandstormSounds()
                && config.isEnabled(WeatherEffectType.SANDSTORM);

        if (!enabled) {
            return;
        }

        final Minecraft gameClient = Minecraft.getInstance();
        final Camera camera = gameClient.gameRenderer.getMainCamera();
        if (camera == null) {
            return; // no camera for whatever reason
        }

        if (world.random.nextInt(3) >= this.timer) {
            this.timer++;
            return;
        }
        this.timer = 0;

        this.chooseSpotForWindSound(world, camera).ifPresent(
                pos -> {
                    world.playLocalSound(
                            pos,
                            ISSoundEvents.WEATHER_STRONG_WIND,
                            SoundSource.WEATHER,
                            0.5f, 1f,
                            false
                    );
                }
        );
    }

    private Optional<BlockPos> chooseSpotForWindSound(ClientLevel world, Camera camera) {
        BlockPos cameraPos = camera.blockPosition();

        int dx = world.random.nextIntBetweenInclusive(-MAX_XZ_OFFSET, MAX_SOUND_Y_DIFF);
        int dy = world.random.nextIntBetweenInclusive(-MAX_XZ_OFFSET, MAX_SOUND_Y_DIFF);
        int dz = world.random.nextIntBetweenInclusive(-MAX_XZ_OFFSET, MAX_SOUND_Y_DIFF);
        BlockPos soundPos = cameraPos.offset(dx, dy, dz);

        WeatherEffectType.WeatherData weatherData = WeatherEffectsClient.getCurrentType(world, soundPos, true)
                .getWeatherData(world);
        if (weatherData != null && weatherData.windy()) {
            return Optional.of(soundPos);
        }

        return Optional.empty();
    }
}