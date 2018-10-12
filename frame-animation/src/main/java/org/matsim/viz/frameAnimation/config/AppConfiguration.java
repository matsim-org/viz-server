package org.matsim.viz.frameAnimation.config;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Getter
public class AppConfiguration extends Configuration {

    @Getter
    @Setter
    private static AppConfiguration instance;

    private String tmpFilePath = "./tmpFiles";

    private String relyingPartyId = "id";
    private String relyingPartySecret = "secret";

    private URI ownHostname = URI.create("http://dumm-hostname.com");
    private URI introspectionEndpoint = URI.create("http://dummy-endpoint.com");
    private URI tokenEndpoint = URI.create("http://dummy-endpoint.com");
    private URI fileServer = URI.create("http://dummy-endpoint.com");


    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();
}
