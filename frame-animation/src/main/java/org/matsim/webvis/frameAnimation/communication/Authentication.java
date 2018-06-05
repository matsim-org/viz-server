package org.matsim.webvis.frameAnimation.communication;

import org.matsim.webvis.common.auth.ClientAuthentication;
import org.matsim.webvis.common.communication.HttpClientFactoryWithTruststore;
import org.matsim.webvis.frameAnimation.config.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Authentication {

    public static final ClientAuthentication Instance = initialize();

    private Authentication() {
    }

    private static ClientAuthentication initialize() {

        Path tlsTruststore = Paths.get(Configuration.getInstance().getTlsTrustStore());
        char[] tlsTruststorePassword = Configuration.getInstance().getTlsTrustStorePassword().toCharArray();
        HttpClientFactoryWithTruststore factory = new HttpClientFactoryWithTruststore(tlsTruststore, tlsTruststorePassword);

        return null;
    }
}
