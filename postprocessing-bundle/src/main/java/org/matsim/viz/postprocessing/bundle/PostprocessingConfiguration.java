package org.matsim.viz.postprocessing.bundle;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;

import java.net.URI;

@Getter
public class PostprocessingConfiguration extends Configuration {

    private String tmpFiles = "./tmpFiles";
    private String relyingPartyId = "id";
    private String relyingPartySecret = "secret";

    private URI ownHostname = URI.create("http://dumm-hostname.com");
    private URI idProvider = URI.create("http://dummy-endpoint.com");
    private URI tokenEndpoint = URI.create("http://dummy-endpoint.com");
    private URI fileServer = URI.create("http://dummy-endpoint.com");

    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();
    private DataSourceFactory database = new DataSourceFactory();
}
