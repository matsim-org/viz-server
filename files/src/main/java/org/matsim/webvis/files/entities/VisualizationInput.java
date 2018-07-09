package org.matsim.webvis.files.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.webvis.database.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VisualizationInput extends AbstractEntity {

    private String key;

    @ManyToOne
    private FileEntry fileEntry;

    @ManyToOne(optional = false)
    private Visualization visualization;
}
