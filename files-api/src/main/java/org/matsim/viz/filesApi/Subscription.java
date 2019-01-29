package org.matsim.viz.filesApi;

import lombok.Getter;

import java.net.URI;
import java.time.Instant;

@Getter
public class Subscription {

    private NotificationType type;
    private URI callback;
    private Instant expiresAt;

    @Getter
    public static class NotificationType {

        private String name;
    }
}
