package org.matsim.webvis.files.entities;

import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Agent extends AbstractEntity {

    @Column(unique = true)
    private String authId;
}
