package org.matsim.webvis.frameAnimation.communication;

import org.matsim.webvis.common.auth.ClientAuthentication;

public class Authentication {

    private static ClientAuthentication instance;

    public static ClientAuthentication Instance() {
        return instance;
    }

    private Authentication() {
    }

    public static void initialize(ClientAuthentication authentication) {

        instance = authentication;
    }
}
