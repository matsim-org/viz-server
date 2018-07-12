package org.matsim.webvis.frameAnimation.config;

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

    private URI introspectionEndpoint;
    private URI tokenEndpoint;
    private URI fileServer;


    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();
}
