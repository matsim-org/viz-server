package org.matsim.viz.filesApi;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URI;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class Subscription {

    private final NotificationType type;
    private final URI callback;
    private final Instant expiresAt;

    @Getter
    @AllArgsConstructor
    public static class NotificationType {

        private final String name;
    }
}
