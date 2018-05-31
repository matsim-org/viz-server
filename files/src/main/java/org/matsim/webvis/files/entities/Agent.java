package org.matsim.webvis.files.entities;

import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Agent extends AbstractEntity {

    @Column(unique = true)
    private String authId;
}
