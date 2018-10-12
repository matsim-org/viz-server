package org.matsim.viz.files.notifications;

import io.dropwizard.auth.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.error.UnauthorizedException;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.ServiceAgent;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.URI;

@Path("/notifications")
public class NotificationResource {

    private final Notifier notifier;

    public NotificationResource(Notifier notifier) {
        this.notifier = notifier;
    }

    @POST
    @Path("/subscribe")
    @Produces(MediaType.APPLICATION_JSON)
    public Subscription subscribe(@Auth Agent agent, @Valid SubscriptionRequest request) {

        if (agent instanceof ServiceAgent)
            return notifier.createSubscription(request.getType(), request.getCallback());
        else
            throw new UnauthorizedException("Only authenticated services may subscribe to notifications");
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class SubscriptionRequest {

        @NotNull
        private String type;
        @NotNull
        private URI callback;
    }
}
