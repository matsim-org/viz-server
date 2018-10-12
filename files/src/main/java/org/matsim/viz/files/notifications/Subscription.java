package org.matsim.viz.files.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.*;
import java.net.URI;
import java.time.Instant;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"type_id", "callback"})})
@Getter
@AllArgsConstructor
@NoArgsConstructor
class Subscription extends AbstractEntity {

    @ManyToOne(optional = false)
    private NotificationType type;

    @Column(nullable = false)
    private URI callback;

    @Setter
    private Instant expiresAt;
}
