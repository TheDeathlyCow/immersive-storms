package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.registry.ISSoundEvents;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffects;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;

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

        if (!ImmersiveStormsClient.getConfig().isEnableStrongWindSounds()) {
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
                soundPos -> {
                    BlockPos cameraPos = BlockPos.containing(camera.position());
                    boolean playAboveSound = soundPos.getY() > cameraPos.getY() + 1
                            && world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPos).getY() > Mth.floor(cameraPos.getY());

                    float volume;
                    float pitch;
                    
                    if (playAboveSound) {
                        volume = 0.1f;
                        pitch = 0.5f;
                    } else {
                        volume = 0.5f;
                        pitch = 1f;
                    }

                    world.playLocalSound(
                            soundPos,
                            ISSoundEvents.WEATHER_STRONG_WIND,
                            SoundSource.WEATHER,
                            volume, pitch,
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

        WeatherEffectType.WeatherData weatherData = WeatherEffects.getCurrentType(
                world,
                soundPos,
                true,
                WeatherEffectsClient::typeAffectsBiome
        ).getWeatherData(world);

        if (weatherData != null && weatherData.windy()) {
            return Optional.of(soundPos);
        }

        return Optional.empty();
    }
}