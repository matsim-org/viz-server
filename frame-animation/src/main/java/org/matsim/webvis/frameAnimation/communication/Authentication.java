package org.matsim.webvis.frameAnimation.communication;

import org.matsim.webvis.common.auth.ClientAuthentication;
import org.matsim.webvis.common.communication.HttpClientFactory;
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
        HttpClientFactory factory = new HttpClientFactory(tlsTruststore, tlsTruststorePassword);

        return new ClientAuthentication(
                Configuration.getInstance().getTokenEndpoint(),
                Configuration.getInstance().getRelyingPartyId(),
                Configuration.getInstance().getRelyingPartySecret(),
                factory
        );
    }
}
