package org.matsim.webvis.auth.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(callSuper = true)
@Data
public class Token extends AbstractEntity {

    @Column(length = 10000)
    String tokenValue;

    @Column(nullable = false)
    String subjectId;

    Instant createdAt = Instant.now();
    Instant expiresAt;

}
