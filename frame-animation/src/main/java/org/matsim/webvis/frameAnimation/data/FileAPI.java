package org.matsim.webvis.frameAnimation.data;

import org.apache.http.Consts;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.communication.ContentType;
import org.matsim.webvis.common.communication.HttpClientFactoryWithTruststore;
import org.matsim.webvis.frameAnimation.config.Configuration;
import org.matsim.webvis.frameAnimation.entities.Visualization;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileAPI {

    private static final Logger logger = LogManager.getLogger();
    private static final HttpClientFactoryWithTruststore clientFactory = createHttpClientFactory();

    private static HttpClientFactoryWithTruststore createHttpClientFactory() {
        Path tlsTruststore = Paths.get(Configuration.getInstance().getTlsTrustStore());
        char[] tlsTruststorePassword = Configuration.getInstance().getTlsTrustStorePassword().toCharArray();
        return new HttpClientFactoryWithTruststore(tlsTruststore, tlsTruststorePassword);
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

        //Visualization[] viz = Http.authenticatedWithJsonResponse(post, clientFactory, Visualization[].class, Authentication.Instance);

        return null;



    }
}
