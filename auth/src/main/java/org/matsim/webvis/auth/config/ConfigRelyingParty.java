package org.matsim.webvis.auth.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigRelyingParty {

    protected String id;
    protected String name;
    protected String secret;
    protected Set<String> scopes;
}
