package com.thedeathlycow.immersive.storms.world;

import com.thedeathlycow.immersive.storms.ImmersiveStormsClient;
import com.thedeathlycow.immersive.storms.util.WeatherEffectType;
import com.thedeathlycow.immersive.storms.util.WeatherEffects;
import com.thedeathlycow.immersive.storms.util.WeatherEffectsClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;

public final class SandstormSounds implements ClientTickEvents.EndLevelTick {
    private static final int MAX_SOUND_Y_DIFF = 10;

    private static final int MAX_XZ_OFFSET = 10;

    private int timer = 0;

    @Override
    public void onEndTick(ClientLevel level) {
        if (level.getRainLevel(1f) < 0.7f || level.tickRateManager().isFrozen()) {
            return;
        }

        if (!ImmersiveStormsClient.getConfig().isEnableStrongWindSounds()) {
            return;
        }

        final Minecraft gameClient = Minecraft.getInstance();
        final Camera camera = gameClient.gameRenderer.mainCamera();
        if (camera == null) {
            return; // no camera for whatever reason
        }

        if (level.getRandom().nextInt(3) >= this.timer) {
            this.timer++;
            return;
        }
        this.timer = 0;

        this.chooseSpotForWindSound(level, camera).ifPresent(
                soundPos -> {
                    BlockPos cameraPos = BlockPos.containing(camera.position());
                    boolean playAboveSound = soundPos.pos().getY() > cameraPos.getY() + 1
                            && level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPos).getY() > Mth.floor(cameraPos.getY());

                    float volume;
                    float pitch;

                    if (playAboveSound) {
                        volume = 0.1f;
                        pitch = 0.5f;
                    } else {
                        volume = 0.5f;
                        pitch = 1f;
                    }

                    level.playLocalSound(
                            soundPos.pos(),
                            soundPos.event(),
                            SoundSource.WEATHER,
                            volume, pitch,
                            false
                    );
                }
        );
    }

    private Optional<SoundPos> chooseSpotForWindSound(ClientLevel level, Camera camera) {
        BlockPos cameraPos = camera.blockPosition();

        RandomSource random = level.getRandom();

        int dx = random.nextIntBetweenInclusive(-MAX_XZ_OFFSET, MAX_SOUND_Y_DIFF);
        int dy = random.nextIntBetweenInclusive(-MAX_XZ_OFFSET, MAX_SOUND_Y_DIFF);
        int dz = random.nextIntBetweenInclusive(-MAX_XZ_OFFSET, MAX_SOUND_Y_DIFF);
        BlockPos blockPos = cameraPos.offset(dx, dy, dz);

        WeatherEffectType.WeatherData weatherData = WeatherEffects.getCurrentType(
                level,
                blockPos,
                true,
                WeatherEffectsClient::typeAffectsBiome
        ).getWeatherData(level);

        SoundEvent windSound = weatherData != null ? weatherData.windSound() : null;
        if (windSound != null) {
            return Optional.of(new SoundPos(windSound, blockPos));
        }

        return Optional.empty();
    }

    private record SoundPos(SoundEvent event, BlockPos pos) {
    }
}