package org.matsim.webvis.files.entities;

import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.security.Principal;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Agent extends AbstractEntity implements Principal {

    @Column(unique = true)
    private String authId;

    public String getName() {
        return authId;
    }
}
