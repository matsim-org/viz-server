package org.matsim.webvis.frameAnimation.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.matsim.webvis.frameAnimation.data.VisualizationData;

@Getter
@AllArgsConstructor
public class ConfigurationResponse {

    private RectContract bounds;
    private double firstTimestep;
    private double lastTimestep;
    private double timestepSize;
    private VisualizationData.Progress progress;

    public ConfigurationResponse(VisualizationData.Progress progress) {
        this.progress = progress;
        firstTimestep = 0;
        lastTimestep = 0;
        timestepSize = 1;
        bounds = new RectContract(0, 0, 0, 0);
    }
}
