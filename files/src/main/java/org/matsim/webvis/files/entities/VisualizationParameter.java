package org.matsim.webvis.files.entities;

import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class VisualizationParameter extends AbstractEntity {

    private String key;
    private String value;
}
