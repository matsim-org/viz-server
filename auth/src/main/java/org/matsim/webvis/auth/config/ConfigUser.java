package org.matsim.webvis.auth.config;

import lombok.Getter;

@Getter
public class ConfigUser {

    public String username;
    public String password;
    public String id;

    public ConfigUser(String username, String id, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
