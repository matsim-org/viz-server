package org.matsim.viz.auth.discovery;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class DiscoveryResourceTest {

    @Test
    public void getOpenIdConfiguration() {

        URI host = URI.create("https://unit-test-host");
        URI expectedAuthEndpoint = host.resolve("/authorize");
        URI expectedTokenEndpoint = host.resolve("/token");
        URI expectedCertEndpoint = host.resolve("/certificates");

        DiscoveryResource resource = new DiscoveryResource(host);

        OpenIdConfiguration configuration = resource.getOpenIdConfiguration();

        assertEquals(host, configuration.getIssuer());
        assertEquals(expectedAuthEndpoint, configuration.getAuthorization_endpoint());
        assertEquals(expectedTokenEndpoint, configuration.getToken_endpoint());
        assertEquals(expectedCertEndpoint, configuration.getJwks_uri());
    }
}
