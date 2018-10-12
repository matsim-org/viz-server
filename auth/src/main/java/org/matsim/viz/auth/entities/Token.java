package org.matsim.viz.auth.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.Instant;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
public class Token extends AbstractEntity {

    @Column(length = 10000)
    String tokenValue;

    @Column(nullable = false)
    String subjectId;

    String scope;

    Instant createdAt = Instant.now();
    Instant expiresAt;

}
