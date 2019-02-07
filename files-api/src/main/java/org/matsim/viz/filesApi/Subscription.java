package org.matsim.viz.filesApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {

    private NotificationType type;
    private URI callback;
    private Instant expiresAt;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotificationType {

        private String name;
    }
}
