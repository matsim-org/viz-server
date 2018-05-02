package org.matsim.webvis.files.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.net.URI;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VisualizationType extends AbstractEntity {

    @Column(unique = true)
    private String key;
    private boolean requiresProcessing;
    private URI endpoint;
}
