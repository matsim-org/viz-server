package org.matsim.webvis.auth.entities;

import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class RelyingParty extends AbstractEntity {

    private String name;
}
