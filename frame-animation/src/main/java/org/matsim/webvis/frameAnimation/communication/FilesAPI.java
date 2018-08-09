package org.matsim.webvis.frameAnimation.communication;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.matsim.webvis.error.UnauthorizedException;
import org.matsim.webvis.frameAnimation.config.AppConfiguration;
import org.matsim.webvis.frameAnimation.entities.Visualization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.UriBuilder;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;

public class FilesAPI {

    private static final Logger logger = LoggerFactory.getLogger(FilesAPI.class);
    private static final URI vizByTypeEndpoint =
            AppConfiguration.getInstance().getFileServer().resolve("/visualizations");
    public static FilesAPI Instance = new FilesAPI();

    public Visualization[] fetchVisualizations(Instant after) {

        Invocation.Builder builder = ServiceCommunication.getClient().target(vizByTypeEndpoint)
                .queryParam("type", "Animation")
                .queryParam("after", after.toString())
                .request();

        return get(builder, Visualization[].class);
    }

    public InputStream fetchFile(String projectId, String fileId) {

        URI endpoint = UriBuilder.fromUri(AppConfiguration.getInstance().getFileServer())
                .path("projects").path(projectId).path("files").path(fileId).build();

        Invocation.Builder builder = ServiceCommunication.getClient().target(endpoint)
                .request();

        return get(builder, InputStream.class);
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
}
