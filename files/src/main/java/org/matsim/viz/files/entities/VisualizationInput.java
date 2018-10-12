package org.matsim.viz.files.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VisualizationInput extends AbstractEntity {

    private String inputKey;

    @ManyToOne
    private FileEntry fileEntry;

    @ManyToOne(optional = false)
    private Visualization visualization;
}
