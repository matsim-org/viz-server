package org.matsim.viz.postprocessing.emissions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.postprocessing.bundle.PersistentVisualization;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Visualization extends PersistentVisualization {

    @OneToMany(mappedBy = "visualization", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    Set<Bin> bins = new HashSet();

    private double cellSize;
    private double smoothingRadius;
    private double timeBinSize;

    /**
     * Add a time bin to the visualization, and link that viz to the bin itself
     * @param b
     */
    void addBin(Bin b) {
        b.setVisualization(this);
        bins.add(b);
    }
}
