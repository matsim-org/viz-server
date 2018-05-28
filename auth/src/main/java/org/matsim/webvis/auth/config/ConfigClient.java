package org.matsim.webvis.auth.config;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

@Getter
@NoArgsConstructor
public class ConfigClient extends ConfigRelyingParty {

    private List<URI> redirectUris;

    public ConfigClient(String name, String id, String secret, List<URI> uris) {
        super(id, name, secret);
        this.redirectUris = uris;
    }
}
