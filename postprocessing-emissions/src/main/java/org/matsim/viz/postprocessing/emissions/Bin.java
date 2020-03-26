package org.matsim.viz.postprocessing.emissions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;
import org.matsim.viz.postprocessing.bundle.PersistentVisualization;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Bin extends AbstractEntity {

    private double startTime;

    @ManyToOne(optional = false)
    private Visualization visualization;

    @Lob
    private String data = "";
}
