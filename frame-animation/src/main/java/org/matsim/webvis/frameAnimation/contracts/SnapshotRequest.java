package org.matsim.webvis.frameAnimation.contracts;

import lombok.Getter;

@Getter
public class SnapshotRequest extends VisualizationRequest {

    double speedFactor;
    private double fromTimestep;
    private int size;

    public SnapshotRequest(double fromTimestep, int size, double speedFactor) {
        this.fromTimestep = fromTimestep;
        this.size = size;
        this.speedFactor = speedFactor;
    }
}
