package org.matsim.viz.frameAnimation.persistenceModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Visualization extends AbstractEntity {

    @OneToOne(mappedBy = "visualization", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private MatsimNetwork matsimNetwork;

    @OneToMany(mappedBy = "visualization", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Snapshot> snapshots = new ArrayList<>();

    private double timestepSize;

    public void addNetwork(MatsimNetwork network) {
        this.matsimNetwork = network;
        network.setVisualization(this);
    }

    public void addSnapshot(Snapshot snapshot) {
        this.snapshots.add(snapshot);
        snapshot.setVisualization(this);
    }
}
