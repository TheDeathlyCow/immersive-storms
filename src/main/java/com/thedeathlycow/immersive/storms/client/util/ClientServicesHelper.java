package com.thedeathlycow.immersive.storms.client.util;

import com.thedeathlycow.immersive.storms.client.particle.ClientParticleHelper;
import com.thedeathlycow.immersive.storms.util.ServicesHelper;

public final class ClientServicesHelper {
    public static final ClientParticleHelper CLIENT_PARTICLE_HELPER = ServicesHelper.load(ClientParticleHelper.class);

    private ClientServicesHelper() {

    }
}