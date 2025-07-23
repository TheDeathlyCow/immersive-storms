package com.thedeathlycow.immersive.storms;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiAfterDhInitEvent;
import com.seibel.distanthorizons.api.methods.events.sharedParameterObjects.DhApiEventParam;
import com.thedeathlycow.immersive.storms.config.ImmersiveStormsConfig;

public final class DistantHorizonsPatch {
    static void bindDhEvents(ImmersiveStormsConfig config) {
        DhApi.events.bind(DhApiAfterDhInitEvent.class, new DhApiAfterDhInitEvent() {
            @Override
            public void afterDistantHorizonsInit(DhApiEventParam<Void> input) {
                if (DhApi.Delayed.configs != null && config.isEnableVanillaFogWithDH()) {
                    DhApi.Delayed.configs.graphics().fog().enableVanillaFog().setValue(true);
                }
            }
        });
    }

    private DistantHorizonsPatch() {

    }
}