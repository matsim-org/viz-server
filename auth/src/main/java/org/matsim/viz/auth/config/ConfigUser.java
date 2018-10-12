package org.matsim.viz.auth.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigUser {

    public String username;
    public String password;
    public String id;
}
