package org.matsim.viz.files.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationType extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String name;
}
