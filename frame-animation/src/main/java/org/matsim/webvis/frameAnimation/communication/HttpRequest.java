package org.matsim.webvis.frameAnimation.communication;

import org.matsim.webvis.common.communication.Http;

public class HttpRequest {

    private static Http instance;

    private HttpRequest() {
    }

    public static Http Instance() {
        return instance;
    }

    public static void initialize(Http http) {
        instance = http;
    }
}
