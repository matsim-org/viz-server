package org.matsim.webvis.frameAnimation.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisualizationData {

    private SimulationData simulationData;
    private Progress progress;

    boolean isDone() {
        return progress == Progress.Done;
    }

    public enum Progress {DownloadingInput, GeneratingData, Done}
}
