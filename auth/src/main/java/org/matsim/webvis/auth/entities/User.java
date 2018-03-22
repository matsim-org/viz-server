package org.matsim.webvis.auth.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class User extends AbstractEntity {

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String eMail;
}
