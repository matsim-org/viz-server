package org.matsim.webvis.frameAnimation.communication;

import org.matsim.webvis.common.auth.ClientAuthentication;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.common.communication.HttpClientFactory;
import org.matsim.webvis.common.communication.HttpClientFactoryWithTruststore;
import org.matsim.webvis.frameAnimation.config.Configuration;

import java.nio.file.Paths;

public class ServiceCommunication {

    private static ClientAuthentication clientAuthentication;
    private static Http http;

    public static ClientAuthentication authentication() {
        return clientAuthentication;
    }

    public static Http http() {
        return http;
    }

    public static void initialize(boolean trustSelfsignedTLSCertificates) {

        initializeHttp(trustSelfsignedTLSCertificates);
        initializeAuthentication();

    }

    private static void initializeHttp(boolean trustSelfsignedTLSCertificates) {

        if (trustSelfsignedTLSCertificates) {
            HttpClientFactory factory = new HttpClientFactoryWithTruststore(
                    Paths.get(Configuration.getInstance().getTlsTrustStore()),
                    Configuration.getInstance().getTlsTrustStorePassword().toCharArray());
            http = new Http(factory);
        } else {
            http = new Http();
        }
    }

    private static void initializeAuthentication() {

        clientAuthentication = new ClientAuthentication(http,
                Configuration.getInstance().getTokenEndpoint(),
                Configuration.getInstance().getRelyingPartyId(),
                Configuration.getInstance().getRelyingPartySecret(),
                "service-client");

        clientAuthentication.requestAccessToken();
    }
}
