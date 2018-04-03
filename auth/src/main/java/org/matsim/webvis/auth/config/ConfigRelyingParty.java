package org.matsim.webvis.auth.config;

import lombok.Getter;

@Getter
public class ConfigRelyingParty {

    protected String id;
    protected String name;
    protected String secret;

    ConfigRelyingParty() {
    }

    public ConfigRelyingParty(String name, String id, String secret) {
        this.name = name;
        this.id = id;
        this.secret = secret;
    }
}
