package org.matsim.webvis.auth.entities;

import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class RelyingParty extends AbstractEntity {

    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> scopes = new HashSet<>();
}
