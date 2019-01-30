package org.matsim.viz.filesApi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.java.Log;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.matsim.viz.clientAuth.ClientAuthentication;
import org.matsim.viz.clientAuth.Credentials;
import org.matsim.viz.database.AbstractEntity;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.UriBuilder;
import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Log
public class FilesApi {

    private final RetryPolicy<Object> unauthorizedPolicy;
    private final Client client;
    private final ClientAuthentication authentication;
    private final URI endpoint;

    FilesApi(Client client, ClientAuthentication authentication, URI filesEndpoint) {
        this.client = client;
        this.authentication = authentication;
        this.endpoint = filesEndpoint;
        this.unauthorizedPolicy = createNotAuthorizedPolicy();
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.addMixIn(AbstractEntity.class, AbstractEntityMixin.class);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public Visualization[] fetchVisualizations(String visualizationType, Instant createdAfter) {

        Invocation.Builder builder = client.target(endpoint.resolve("/visualizations"))
                .queryParam("type", visualizationType)
                .queryParam("after", createdAfter.toString())
                .request();
        return authenticatedRequest(builder, request -> request.get(Visualization[].class));
    }

    public InputStream downloadFile(String projectId, String fileId) {

        URI resource = UriBuilder.fromUri(endpoint).path("projects").path(projectId).path("files")
                .path(fileId).build();
        Invocation.Builder builder = client.target(resource).request();
        return authenticatedRequest(builder, request -> request.get(InputStream.class));
    }

    public Subscription registerNotification(String type, URI callback) {

        URI resource = UriBuilder.fromUri(endpoint).path("notifications").path("subscribe").build();

        Invocation.Builder builder = client.target(resource).request();
        Entity<SubscriptionRequest> entity = Entity.json(new SubscriptionRequest(type, callback));
        return authenticatedRequest(builder, request -> request.post(entity, Subscription.class));
    }

    private <T> T authenticatedRequest(Invocation.Builder requestBuilder, Function<Invocation.Builder, T> request) {

        requestAccessTokenIfNonePresent();
        return Failsafe.with(unauthorizedPolicy).get(() -> {
                    Invocation.Builder withProperty = requestBuilder.property(OAuth2ClientSupport.OAUTH2_PROPERTY_ACCESS_TOKEN, authentication.getAccessToken());
                    return request.apply(withProperty);
                }
        );
    }

    private void requestAccessTokenIfNonePresent() {
        if (!authentication.hasAccessToken())
            authentication.requestAccessToken();
    }

    private RetryPolicy<Object> createNotAuthorizedPolicy() {
        return new RetryPolicy<>()
                .handle(NotAuthorizedException.class)
                .withMaxRetries(1)
                .onFailedAttempt(e -> authentication.requestAccessToken());
    }

    @AllArgsConstructor
    @Getter
    private static class SubscriptionRequest {

        private String type;
        private URI callback;
    }

    public static class FilesApiBuilder {

        private String relyingPartyId;
        private String relyingPartySecret;
        private URI tokenEndpoint;
        private URI filesEndpoint;
        private Client client;

        public FilesApiBuilder withRelyingPartyId(String id) {
            this.relyingPartyId = id;
            return this;
        }

        public FilesApiBuilder withRelyingPartySecret(String secret) {
            this.relyingPartySecret = secret;
            return this;
        }

        public FilesApiBuilder withTokenEndpoint(URI tokenEndpoint) {
            this.tokenEndpoint = tokenEndpoint;
            return this;
        }

        public FilesApiBuilder withFilesEndpoint(URI filesServerRoot) {
            this.filesEndpoint = filesServerRoot;
            return this;
        }

        public FilesApiBuilder withClient(Client client) {
            this.client = client;
            return this;
        }

        public FilesApi build() {

            registerAuthentication(requireNonNull(client));
            ClientAuthentication authentication = createClientAuthentication(
                    client,
                    requireNonNull(tokenEndpoint),
                    requireNonNull(relyingPartyId),
                    requireNonNull(relyingPartySecret));
            return new FilesApi(client, authentication, requireNonNull(filesEndpoint));
        }

        private void registerAuthentication(Client client) {

            HttpAuthenticationFeature basicAuth = HttpAuthenticationFeature.basicBuilder().nonPreemptive().build();
            client.register(basicAuth);
            Feature oauthFeature = OAuth2ClientSupport.feature(null);
            client.register(oauthFeature);
        }

        private ClientAuthentication createClientAuthentication(
                Client client, URI tokenEndpoint, String relyingPartyId, String relyingPartySecret) {

            return new ClientAuthentication(client, tokenEndpoint, "service-client",
                    new Credentials(relyingPartyId, relyingPartySecret));
        }
    }
}
