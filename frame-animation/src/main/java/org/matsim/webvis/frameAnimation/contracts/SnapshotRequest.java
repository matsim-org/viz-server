package org.matsim.webvis.frameAnimation.contracts;

import lombok.Getter;

@Getter
public class SnapshotRequest extends VisualizationRequest {

    double speedFactor;
    private double fromTimestep;
    private int size;

    public SnapshotRequest(String vizId, double fromTimestep, int size, double speedFactor) {
        super(vizId);
        this.fromTimestep = fromTimestep;
        this.size = size;
        this.speedFactor = speedFactor;
        this.id = vizId;
    }
}
