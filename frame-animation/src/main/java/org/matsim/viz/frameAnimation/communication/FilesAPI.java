package org.matsim.viz.frameAnimation.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.matsim.viz.error.UnauthorizedException;
import org.matsim.viz.frameAnimation.config.AppConfiguration;
import org.matsim.viz.frameAnimation.entities.Subscription;
import org.matsim.viz.frameAnimation.entities.Visualization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.UriBuilder;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.function.Function;

@RequiredArgsConstructor
public class FilesAPI {

    private static final Logger logger = LoggerFactory.getLogger(FilesAPI.class);

    private final URI vizByTypeEndpoint;

    public Visualization[] fetchVisualizations(Instant after) {

        Invocation.Builder builder = ServiceCommunication.getClient().target(vizByTypeEndpoint)
                .queryParam("type", "frame-animation")
                .queryParam("after", after.toString())
                .request();

        // return get(builder, Visualization[].class);
        return authenticatedRequest(builder, request -> request.get(Visualization[].class));
    }

    public InputStream fetchFile(String projectId, String fileId) {

        URI endpoint = UriBuilder.fromUri(AppConfiguration.getInstance().getFileServer())
                .path("projects").path(projectId).path("files").path(fileId).build();

        Invocation.Builder builder = ServiceCommunication.getClient().target(endpoint)
                .request();

        //return get(builder, InputStream.class);
        return authenticatedRequest(builder, request -> request.get(InputStream.class));
    }

    Subscription registerNotfication(String type, URI callback) {

        URI endpoint = UriBuilder.fromUri(AppConfiguration.getInstance().getFileServer())
                .path("notifications").path("subscribe").build();

        Invocation.Builder builder = ServiceCommunication.getClient().target(endpoint)
                .request();

        Entity<SubscriptionRequest> entity = Entity.json(new SubscriptionRequest(type, callback));
        return authenticatedRequest(builder, request -> request.post(entity, Subscription.class));
    }

    private <T> T get(Invocation.Builder requestBuilder, Class<T> responseType) {

        try {
            return requestBuilder
                    .property(OAuth2ClientSupport.OAUTH2_PROPERTY_ACCESS_TOKEN, ServiceCommunication.getAuthentication().getAccessToken())
                    .get(responseType);
        } catch (NotAuthorizedException e) {
            logger.error("Not authorized! Attempting to refresh access_token");
            ServiceCommunication.getAuthentication().requestAccessToken();
            throw new UnauthorizedException("Could not authenticate at endpoint");
        }
    }

    private <T> T authenticatedRequest(Invocation.Builder requestBuilder, Function<Invocation.Builder, T> requestInvokation) {

        try {
            requestBuilder = requestBuilder.property(OAuth2ClientSupport.OAUTH2_PROPERTY_ACCESS_TOKEN, ServiceCommunication.getAuthentication().getAccessToken());
            return requestInvokation.apply(requestBuilder);
        } catch (NotAuthorizedException e) {
            logger.error("Not authorized! Attempting to refresh access_token");
            ServiceCommunication.getAuthentication().requestAccessToken();
            throw new UnauthorizedException("Could not authenticate at endpoint");
        }
    }

    @AllArgsConstructor
    @Getter
    private static class SubscriptionRequest {

        private String type;
        private URI callback;
    }
}
