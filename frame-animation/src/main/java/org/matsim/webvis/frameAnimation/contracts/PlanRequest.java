package org.matsim.webvis.frameAnimation.contracts;

import lombok.Getter;

@Getter
public class PlanRequest extends VisualizationRequest {

    private int idIndex;

    public PlanRequest(String vizId, int idIndex) {
        super(vizId);
        this.idIndex = idIndex;
    }
}
