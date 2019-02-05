package org.matsim.viz.postprocessing.emissions.persistenceModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.postprocessing.bundle.PersistentVisualization;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Visualization extends PersistentVisualization {

    @Lob
    private String data = "";

    private double cellSize;
    private double smoothingRadius;
    private double timeBinSize;
}
