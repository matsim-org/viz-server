package org.matsim.webvis.frameAnimation.requestHandling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.frameAnimation.data.DataController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/notifications")
public class NotificationResource {

    private final DataController dataController;
    private final Client client;
    private final ExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public NotificationResource(DataController dataController, Client client, URI subscribeEndpoint) {
        this.dataController = dataController;
        this.client = client;
        this.registerCallback(subscribeEndpoint);
    }

    @POST
    public void visualizationCallback(@Valid Notification notification) {

        switch (notification.getType()) {
            case "visualization_created":
                this.dataController.fetchVisualizations();
            default:
                throw new InternalException("Unknown notification type: " + notification.getType());
        }

    }

    private void registerCallback(URI endpoint) {

        /*client.target(endpoint.toString()).queryParam("type", "visualization_created")
                .queryParam("callback")*/
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Notification {

        @NotNull
        private String type;
        private String message;
    }
}
