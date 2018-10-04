package org.matsim.webvis.files.notifications;

import io.dropwizard.auth.Auth;
import org.matsim.webvis.error.UnauthorizedException;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.ServiceAgent;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    public Subscription subscribe(@Auth Agent agent, @QueryParam("type") String type, @QueryParam("callback") URI callback) {

        if (agent instanceof ServiceAgent)
            return notifier.createSubscription(type, callback);
        else
            throw new UnauthorizedException("Only authenticated services may subscribe to notifications");
    }
}
