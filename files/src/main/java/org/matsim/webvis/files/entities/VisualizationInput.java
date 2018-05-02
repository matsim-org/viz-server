package org.matsim.webvis.files.entities;

import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class VisualizationInput extends AbstractEntity {

    private String key;

    @ManyToOne
    private FileEntry fileEntry;

    @ManyToOne(optional = false)
    private Visualization visualization;
}
