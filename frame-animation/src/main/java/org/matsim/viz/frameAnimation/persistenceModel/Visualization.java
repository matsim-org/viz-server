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

    @OneToMany(mappedBy = "visualization", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Plan> plans = new ArrayList<>();

    private String filesServerId;
    private double timestepSize;
    private double firstTimestep = Double.MAX_VALUE;
    private double lastTimestep = Double.MIN_VALUE;
    private double minEasting;
    private double maxEasting;
    private double minNorthing;
    private double maxNorthing;
    private Progress progress = Progress.DownloadingInput;

    public void addNetwork(MatsimNetwork network) {
        this.matsimNetwork = network;
        minEasting = network.getMinEasting();
        maxEasting = network.getMaxEasting();
        minNorthing = network.getMinNorthing();
        maxNorthing = network.getMaxNorthing();
        network.setVisualization(this);
    }

    public void addSnapshot(Snapshot snapshot) {
        this.snapshots.add(snapshot);
        snapshot.setVisualization(this);
    }

    public void addPlan(Plan plan) {
        this.plans.add(plan);
        plan.setVisualization(this);
    }

    public enum Progress {DownloadingInput, GeneratingData, Done, Failed}
}
