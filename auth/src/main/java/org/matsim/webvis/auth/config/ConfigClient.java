package org.matsim.webvis.auth.config;

import lombok.Getter;

import java.net.URI;
import java.util.List;

@Getter
public class ConfigClient extends ConfigRelyingParty {
    private List<URI> redirectUris;

    public ConfigClient() {
    }

    public ConfigClient(String name, String id, String secret, List<URI> uris) {
        super(name, id, secret);
        this.redirectUris = uris;
    }
}
