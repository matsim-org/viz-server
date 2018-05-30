package org.matsim.webvis.frameAnimation.data;

import com.google.common.reflect.TypeToken;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.auth.BasicAuthentication;
import org.matsim.webvis.common.auth.PrincipalCredentialToken;
import org.matsim.webvis.common.communication.ContentType;
import org.matsim.webvis.common.communication.HttpClientFactory;
import org.matsim.webvis.common.communication.HttpRequest;
import org.matsim.webvis.common.service.InternalException;
import org.matsim.webvis.frameAnimation.communication.Authentication;
import org.matsim.webvis.frameAnimation.config.Configuration;
import org.matsim.webvis.frameAnimation.contracts.geoJSON.GeoJsonElement;
import org.matsim.webvis.frameAnimation.entities.Visualization;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Collection;

public class FileAPI {

    private static final Logger logger = LogManager.getLogger();
    private static final HttpClientFactory clientFactory = createHttpClientFactory();

    private static HttpClientFactory createHttpClientFactory() {
        Path tlsTruststore = Paths.get(Configuration.getInstance().getTlsTrustStore());
        char[] tlsTruststorePassword = Configuration.getInstance().getTlsTrustStorePassword().toCharArray();
        return new HttpClientFactory(tlsTruststore, tlsTruststorePassword);
    }

   /* static InputStream downloadFile(String fileId, String projectId) {

        URI fileEndpoint = Configuration.getInstance().getFileServer().resolve("file/");
        HttpPost post = new HttpPost(fileEndpoint);
        final String auth = BasicAuthentication.encodeToAuthorizationHeader(new PrincipalCredentialToken(
                Configuration.getInstance().getRelyingPartyId(), Configuration.getInstance().getRelyingPartySecret()
        ));
        post.addHeader(BasicAuthentication.HEADER_AUTHORIZATION, auth);

        try {
            return makeRequest(post).getContent();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }*/

    public static Visualization fetchVisualizations() {
        URI endpoint = Configuration.getInstance().getFileServer().resolve("/project/visualizations/");
        HttpPost post = new HttpPost(endpoint);
        post.addHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON);
        post.setEntity(new StringEntity("{ 'visualizationType': 'Animation' }", Consts.UTF_8));

        Visualization[] viz = HttpRequest.authenticatedWithJsonResponse(post, clientFactory, Visualization[].class, Authentication.Instance);

        return null;



    }
}
