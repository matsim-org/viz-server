package org.matsim.viz.postprocessing.bundle;

import lombok.extern.java.Log;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.filesApi.Notification;
import org.matsim.viz.filesApi.Subscription;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log
@Path("/notifications")
public class NotificationResource {

    private static final RetryPolicy<Object> retryPolicy = new RetryPolicy<>()
            .handle(Exception.class)
            .withBackoff(1, 60, ChronoUnit.SECONDS, 4)
            .withMaxAttempts(12);

    private final FilesApi filesApi;
    private final VisualizationFetcher fetcher;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public NotificationResource(FilesApi filesApi, URI selfHostname, VisualizationFetcher fetcher) {
        this.filesApi = filesApi;
        this.fetcher = fetcher;
        scheduler.schedule(() -> registerCallback(selfHostname), 0, TimeUnit.SECONDS);
    }

    @POST
    public void callback(@Valid Notification notification) {

        switch (notification.getType()) {
            case "visualization_created":
                scheduler.schedule(fetcher::fetchVisualizationData, 0, TimeUnit.SECONDS);
                break;
            default:
                throw new RuntimeException("Not implemented");
        }
    }

    private void registerCallback(URI selfHostname) {
        log.info("Attempting to register for notifications");

        URI callback = selfHostname.resolve("/notifications");
        Subscription result = Failsafe.with(retryPolicy)
                .get(() -> filesApi.registerNotification("visualization_created", callback));

        log.info("successfully registered for notifications of type: 'visualization_created'");
        scheduleSubscriptionRefresh(result, selfHostname);
    }

    private void scheduleSubscriptionRefresh(Subscription subscription, URI selfHostname) {

        // schedule refresh of subscription 30 minutes before it expires
        Duration duration = Duration.between(Instant.now(), subscription.getExpiresAt()).minus(Duration.ofMinutes(30));
        scheduler.schedule(() -> registerCallback(selfHostname), duration.getSeconds(), TimeUnit.SECONDS);
        log.info("Scheduled refreshing of subscription '" + subscription.getType().getName() + "' in " + duration.getSeconds() + " seconds");
    }
}
