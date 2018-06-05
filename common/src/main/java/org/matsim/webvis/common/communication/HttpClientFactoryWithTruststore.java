package org.matsim.webvis.common.communication;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.nio.file.Path;

public class HttpClientFactoryWithTruststore implements HttpClientFactory {

    private static Logger logger = LogManager.getLogger();
    private SSLConnectionSocketFactory sslFactory;

    public HttpClientFactoryWithTruststore(Path tlsTruststore, char[] tlsTruststorePassword) {
        sslFactory = initializeSSL(tlsTruststore, tlsTruststorePassword);
    }

    private SSLConnectionSocketFactory initializeSSL(Path tlsTruststore, char[] tlsTruststorePassword) {

        try {
            SSLContext context = SSLContexts.custom()
                    .loadTrustMaterial(tlsTruststore.toFile(), tlsTruststorePassword, new TrustSelfSignedStrategy())
                    .build();
            return new SSLConnectionSocketFactory(
                    context, new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                    null, (String hostname, SSLSession session) -> hostname.equals("localhost")
            );
        } catch (Exception e) {
            logger.error("Failed to initialize SSLFactory for selfsigned certificates.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CloseableHttpClient createClient() {
        return HttpClients.custom().setSSLSocketFactory(sslFactory).build();
    }
}
