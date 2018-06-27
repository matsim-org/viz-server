package org.matsim.webvis.auth.config;

import io.dropwizard.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AuthConfiguration extends Configuration {

    @Setter
    @Getter
    private static AuthConfiguration instance;

    private List<ConfigUser> users = new ArrayList<>();
    private List<ConfigClient> clients = new ArrayList<>();
    private List<ConfigRelyingParty> protectedResources = new ArrayList<>();

    private String tokenSigningKeyStore = "";
    private String tokenSigningKeyStorePassword = "";
    private String tokenSigningKeyAlias = "";
}
