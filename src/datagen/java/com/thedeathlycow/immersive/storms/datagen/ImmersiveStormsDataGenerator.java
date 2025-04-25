package com.thedeathlycow.immersive.storms.datagen;

import com.thedeathlycow.immersive.storms.ImmersiveStorms;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImmersiveStormsDataGenerator implements DataGeneratorEntrypoint {
    public static final String MOD_ID = ImmersiveStorms.MOD_ID + "-datagen";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        LOGGER.info("Running Immersive Storms datagen");
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ISBiomeTagGenerator::new);
        pack.addProvider(ISBlockTagGenerator::new);
        pack.addProvider(EnglishUSGenerator::new);
    }

    @Override
    @Nullable
    public String getEffectiveModId() {
        return ImmersiveStorms.MOD_ID;
    }
}