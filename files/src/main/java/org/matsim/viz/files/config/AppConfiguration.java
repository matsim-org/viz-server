package org.matsim.viz.files.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Getter
public class AppConfiguration extends Configuration {

    @Getter
    @Setter
    private static AppConfiguration instance;

    private URI idProvider = URI.create("https://localhost:3000");
    private String relyingPartyId = "relyingPartyId";
    private String relyingPartySecret = "secret";
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    private DataSourceFactory database = new DataSourceFactory();
    @JsonProperty("repository")
    private RepositoryFactory repositoryFactory = new LocalRepositoryFactory();
}
