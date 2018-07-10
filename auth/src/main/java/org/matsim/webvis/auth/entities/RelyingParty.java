package org.matsim.webvis.auth.entities;

import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.database.AbstractEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.security.auth.Subject;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class RelyingParty extends AbstractEntity implements Principal {

    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> scopes = new HashSet<>();

    @Override
    public boolean implies(Subject subject) {
        return subject.getPrincipals().contains(this);
    }
}
