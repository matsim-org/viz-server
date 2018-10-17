package org.matsim.viz.auth.config;

import io.dropwizard.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AppConfiguration extends Configuration {

    @Setter
    @Getter
    private static AppConfiguration instance;

    private List<ConfigUser> users = new ArrayList<>();
    private List<ConfigClient> clients = new ArrayList<>();
    private List<ConfigRelyingParty> protectedResources = new ArrayList<>();

    protected String tokenSigningKeyStore = "";
    protected String tokenSigningKeyStorePassword = "";
    protected String tokenSigningKeyAlias = "";

    private URI hostURI = URI.create("https://localhost:3000");
}
