package com.thedeathlycow.immersive.storms.datagen;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.Nullable;

public class ImmersiveStormsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {

    }

    @Override
    @Nullable
    public String getEffectiveModId() {
        return ImmersiveStorms.MOD_ID;
    }
}