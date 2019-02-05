package org.matsim.viz.postprocessing.emissions;

import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;
import org.matsim.viz.postprocessing.bundle.PostprocessingConfiguration;

import java.net.URI;

@Getter
public class AppConfiguration extends PostprocessingConfiguration {

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
