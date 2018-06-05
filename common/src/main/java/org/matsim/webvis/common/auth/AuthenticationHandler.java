package org.matsim.webvis.common.auth;

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
import org.matsim.webvis.common.errorHandling.InternalException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
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

        AuthenticatedRequest authenticatedRequest = new AuthenticatedRequest(request);
        AuthenticationResult result = introspectToken(authenticatedRequest.getToken());
        if (result.isActive())
            AuthenticationResult.intoRequestAttribute(request, result);
        else
            throw new UnauthorizedException("Token is invalid");
    }

    private AuthenticationResult introspectToken(String token) {

        HttpPost post = createIntrospectionRequest(token);
        try (CloseableHttpClient client = createHttpClient()) {
            return makeIntrospectionRequest(client, post);
        } catch (IOException e) {
            throw new InternalException("Could not connect to auth server.");
        }
    }

    private AuthenticationResult makeIntrospectionRequest(CloseableHttpClient client, HttpPost post) throws IOException {
        try (CloseableHttpResponse response = client.execute(post)) {
            if (isStatusOk(response)) {
                String message = EntityUtils.toString(response.getEntity());
                return gson.fromJson(message, AuthenticationResult.class);
            } else {
                throw new InternalException("Could not authenticate at auth server");
            }
        }
    }

    private HttpPost createIntrospectionRequest(String token) {

        URI reqURI;
        try {
            reqURI = new URIBuilder(introspectionEndpoint).setParameter("token", token).build();
        } catch (URISyntaxException e) {
            throw new InternalException("Could not create URI.");
        }
        HttpPost post = new HttpPost(reqURI);
        final String basicAuthentication = BasicAuthentication.encodeToAuthorizationHeader(
                new PrincipalCredentialToken(relyingPartyId, relyingPartySecret));
        post.addHeader(BasicAuthentication.HEADER_AUTHORIZATION, basicAuthentication);
        return post;
    }

    CloseableHttpClient createHttpClient() {
        return HttpClients.custom().setSSLSocketFactory(sslFactory).build();
    }

    private boolean isStatusOk(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
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
