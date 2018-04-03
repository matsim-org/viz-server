package org.matsim.webvis.files.communication;

import com.google.gson.Gson;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.ErrorResponse;
import org.matsim.webvis.common.communication.RequestException;
import spark.Filter;
import spark.Request;
import spark.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;

import static spark.Spark.halt;

public class AuthenticationHandler implements Filter {

    private static Logger logger = LogManager.getLogger();
    private static Gson gson = new Gson();

    private SSLConnectionSocketFactory sslFactory;
    private URI introspectionEndpoint;
    private String relyingPartyId;
    private String relyingPartySecret;

    AuthenticationHandler(AuthenticationHandlerBuilder builder) {

        try {
            initializeSSL(builder.trustStore, builder.trustStorePassword);
        } catch (Exception e) {
            logger.error("Error while initializing AuthenticationHandler", e);
            throw new RuntimeException(e);
        }
        this.introspectionEndpoint = builder.introspectionEndpoint;
        this.relyingPartyId = builder.relyingPartyId;
        this.relyingPartySecret = builder.relyingPartySecret;
    }

    public static AuthenticationHandlerBuilder builder() {
        return new AuthenticationHandlerBuilder();
    }

    void initializeSSL(Path trustStore, char[] password)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {

        SSLContext context = SSLContexts.custom()
                .loadTrustMaterial(trustStore.toFile(), password, new TrustSelfSignedStrategy())
                .build();
        sslFactory = new SSLConnectionSocketFactory(
                context, new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                null, (String hostname, SSLSession session) -> hostname.equals("localhost")
        );
    }

    @Override
    public void handle(Request request, Response response) {

        AuthenticationResult authResponse;
        try {
            AuthenticatedRequest authenticatedRequest = new AuthenticatedRequest(request);
            authResponse = introspectToken(authenticatedRequest.getToken());
        } catch (RequestException e) {
            haltWithUnauthorizedError(e.getErrorCode(), e.getMessage(), response);
            return;
        } catch (RuntimeException e) {
            haltWithInternalError(ErrorCode.UNSPECIFIED_ERROR, e.getMessage(), response);
            return;
        }

        if (authResponse.isActive()) {
            Subject.setAuthenticationAsAttribute(request, authResponse);
        } else {
            haltWithUnauthorizedError(ErrorCode.INVALID_TOKEN, "Token is invalid", response);
        }
    }

    private AuthenticationResult introspectToken(String token) {

        HttpPost post = createIntrospectionRequest(token);
        try (CloseableHttpClient client = createHttpClient()) {
            return makeIntrospectionRequest(client, post);
        } catch (IOException e) {
            throw new RuntimeException("Error during http call");
        }
    }

    private AuthenticationResult makeIntrospectionRequest(CloseableHttpClient client, HttpPost post) throws IOException {
        try (CloseableHttpResponse response = client.execute(post)) {
            if (isStatusOk(response)) {
                String message = EntityUtils.toString(response.getEntity());
                return gson.fromJson(message, AuthenticationResult.class);
            } else {
                throw new RuntimeException("Could not authenticate at auth server");
            }
        }
    }

    private HttpPost createIntrospectionRequest(String token) {

        URI reqURI;
        try {
            reqURI = new URIBuilder(introspectionEndpoint).setParameter("token", token).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not create URI.");
        }
        HttpPost post = new HttpPost(reqURI);
        final String basicAuthentication = "Basic " +
                Base64.getEncoder().encodeToString((relyingPartyId + ":" + relyingPartySecret).getBytes());

        post.addHeader("Authorization", basicAuthentication);
        return post;
    }

    CloseableHttpClient createHttpClient() {
        return HttpClients.custom().setSSLSocketFactory(sslFactory).build();
    }

    private boolean isStatusOk(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    private void haltWithInternalError(String errorCode, String message, Response response) {
        haltWithError(errorCode, message, response, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    private void haltWithUnauthorizedError(String errorCode, String message, Response response) {
        haltWithError(errorCode, message, response, HttpStatus.SC_UNAUTHORIZED);
    }

    private void haltWithError(String errorCode, String message, Response response, int status) {
        response.type("application/json");
        response.header("WWW-Authenticate", "Bearer");
        ErrorResponse error = new ErrorResponse(errorCode, message);
        //noinspection ThrowableNotThrown
        halt(status, gson.toJson(error));
    }

    @Setter
    public static class AuthenticationHandlerBuilder {

        private URI introspectionEndpoint;
        private String relyingPartyId;
        private String relyingPartySecret;
        private Path trustStore;
        private char[] trustStorePassword;

        public AuthenticationHandler build() {
            return new AuthenticationHandler(this);
        }

        public AuthenticationHandlerBuilder setIntrospectionEndpoint(URI endpoint) {
            this.introspectionEndpoint = endpoint;
            return this;
        }

        public AuthenticationHandlerBuilder setRelyingPartyId(String id) {
            this.relyingPartyId = id;
            return this;
        }

        public AuthenticationHandlerBuilder setRelyingPartySecret(String secret) {
            this.relyingPartySecret = secret;
            return this;
        }

        public AuthenticationHandlerBuilder setTrustStore(Path trustStore) {
            this.trustStore = trustStore;
            return this;
        }

        public AuthenticationHandlerBuilder setTrustStorePassword(char[] password) {
            this.trustStorePassword = password;
            return this;
        }
    }
}
