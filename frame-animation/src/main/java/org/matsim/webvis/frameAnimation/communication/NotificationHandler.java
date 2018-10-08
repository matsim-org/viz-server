package org.matsim.webvis.frameAnimation.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.frameAnimation.data.DataController;
import org.matsim.webvis.frameAnimation.data.DataProvider;
import org.matsim.webvis.frameAnimation.entities.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Path("/notifications")
public class NotificationHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationHandler.class);

    private final DataController dataController;
    private final DataProvider dataProvider;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public NotificationHandler(DataController dataController, DataProvider dataProvider, URI selfHostname) {
        this.dataController = dataController;
        this.dataProvider = dataProvider;
        this.registerCallback(selfHostname);
    }

    @POST
    public void visualizationCallback(@Valid Notification notification) {

        switch (notification.getType()) {
            case "visualization_created":
                this.dataController.fetchVisualizations();
                break;
            case "visualization_deleted":
                this.dataProvider.remove(notification.getMessage());
                break;
            default:
                throw new InternalException("Unknown notification type: " + notification.getType());
        }

    }

    private void registerCallback(URI selfHostname) {

        logger.info("Attempting to register for notifications");
        try {
            URI callback = selfHostname.resolve("/notifications");
            Subscription result = FilesAPI.Instance.registerNotfication("visualization_created", callback);
            scheduleSubscriptionRefresh(result, selfHostname);
        } catch (Exception e) {
            logger.error("Failed to register for notifications try again in 5 minutes");
            scheduler.schedule(() -> registerCallback(selfHostname), 5, TimeUnit.MINUTES);
        }
    }

    private void scheduleSubscriptionRefresh(Subscription subscription, URI selfHostname) {

        // schedule refresh of subscription 30 minutes before it expires
        Duration duration = Duration.between(Instant.now(), subscription.getExpiresAt()).minus(Duration.ofMinutes(30));
        scheduler.schedule(() -> registerCallback(selfHostname), duration.getSeconds(), TimeUnit.SECONDS);
        logger.info("Scheduled refreshing of subscription '" + subscription.getType().getName() + "' in " + duration.getSeconds() + " seconds");
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class Notification {

        @NotNull
        private String type;
        private String message;
    }
}
