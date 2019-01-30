package org.matsim.viz.postprocessing.emissions;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class AppConfiguration extends Configuration {

    private Path tmpFiles = Paths.get("./tmpFiles");
    private String relyingPartyId = "id";
    private String relyingPartySecret = "secret";

    private URI ownHostname = URI.create("http://dumm-hostname.com");
    private URI idProvider = URI.create("http://dummy-endpoint.com");
    private URI tokenEndpoint = URI.create("http://dummy-endpoint.com");
    private URI fileServer = URI.create("http://dummy-endpoint.com");

    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();
    private DataSourceFactory database = new DataSourceFactory();
}
