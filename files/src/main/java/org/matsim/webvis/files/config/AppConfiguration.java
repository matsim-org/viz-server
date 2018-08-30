package org.matsim.webvis.files.config;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.database.DbConfiguration;
import org.matsim.webvis.files.entities.VisualizationType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AppConfiguration extends Configuration {

    @Getter
    @Setter
    private static AppConfiguration instance;

    private String uploadFilePath = "./files";
    private String tmpFilePath = "./tmp";
    private URI introspectionEndpoint = URI.create("https://localhost:3000/introspect/");
    private String relyingPartyId = "relyingPartyId";
    private String relyingPartySecret = "secret";
    private List<VisualizationType> vizTypes = new ArrayList<>();
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();
    private DbConfiguration database = new DbConfiguration();
}
