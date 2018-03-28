package org.matsim.webvis.auth.config;

import lombok.Getter;
import org.matsim.webvis.auth.entities.RedirectUri;

import java.util.List;

@Getter
public class ConfigClient extends ConfigRelyingParty {
    private List<RedirectUri> redirectUris;
}
