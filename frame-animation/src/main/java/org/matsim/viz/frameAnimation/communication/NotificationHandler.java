package org.matsim.viz.frameAnimation.communication;

import lombok.*;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.filesApi.Subscription;
import org.matsim.viz.frameAnimation.inputProcessing.VisualizationFetcher;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
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

@RequiredArgsConstructor
@Path("/notifications")
public class NotificationHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationHandler.class);

    private final FilesApi filesApi;
    private final VisualizationFetcher visualizationFetcher;
    private final EntityManagerFactory emFactory;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public NotificationHandler(FilesApi filesApi, VisualizationFetcher visualizationFetcher, URI selfHostname, EntityManagerFactory emFactory) {
        this.visualizationFetcher = visualizationFetcher;
        this.emFactory = emFactory;
        this.filesApi = filesApi;
        this.registerCallback(selfHostname);
    }

    @POST
    public void visualizationCallback(@Valid Notification notification) {

        switch (notification.getType()) {
            case "visualization_created":
                this.visualizationFetcher.fetchVisualizations();
                break;
            case "visualization_deleted":
                removeVisualization(notification.getMessage());
                break;
            default:
                throw new InternalException("Unknown notification type: " + notification.getType());
        }
    }

    private void registerCallback(URI selfHostname) {

        logger.info("Attempting to register for notifications");
        try {
            URI callback = selfHostname.resolve("/notifications");
            Subscription result = filesApi.registerNotification("visualization_created", callback);
            scheduleSubscriptionRefresh(result, selfHostname);
        } catch (Exception e) {
            logger.error("Failed to register for notifications try again in 5 minutes");
            scheduler.schedule(() -> registerCallback(selfHostname), 5, TimeUnit.MINUTES);
        }
    }

    private void removeVisualization(String vizId) {

        val em = emFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.getReference(Visualization.class, vizId));
            em.getTransaction().commit();
        } finally {
            em.close();
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
