package org.matsim.viz.frameAnimation.data;

import lombok.Getter;
import lombok.Setter;
import org.matsim.viz.frameAnimation.entities.Permission;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class VisualizationData {

    private SimulationData simulationData;
    private Progress progress;
    private Set<Permission> permissions = new HashSet<>();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isDone() {
        return progress == Progress.Done;
    }

    public enum Progress {DownloadingInput, GeneratingData, Done, Failed}
}
