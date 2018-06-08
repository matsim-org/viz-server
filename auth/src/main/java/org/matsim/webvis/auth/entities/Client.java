package org.matsim.webvis.auth.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Client extends RelyingParty {

    private ClientType type = ClientType.Confidential;

    @OneToMany(mappedBy = "client", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<RedirectUri> redirectUris = new HashSet<>();

    enum ClientType {Confidential, Public}

    public void addRedirectUri(RedirectUri uri) {
        redirectUris.add(uri);
        uri.setClient(this);
    }
}
