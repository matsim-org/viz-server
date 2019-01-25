package org.matsim.viz.frameAnimation.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;

@Getter
@AllArgsConstructor
public class ConfigurationResponse {

    private RectContract bounds;
    private double firstTimestep;
    private double lastTimestep;
    private double timestepSize;
    private Visualization.Progress progress;

    private ConfigurationResponse(Visualization.Progress progress) {
        this.progress = progress;
        firstTimestep = 0;
        lastTimestep = 0;
        timestepSize = 1;
        bounds = new RectContract(0, 0, 0, 0);
    }

    public static ConfigurationResponse createForProgressNotDone(Visualization.Progress progress) {
        return new ConfigurationResponse(progress);
    }

    public static ConfigurationResponse createFromVisualization(Visualization visualization) {
        return new ConfigurationResponse(
                new RectContract(
                        visualization.getMinEasting(), visualization.getMaxEasting(),
                        visualization.getMinNorthing(), visualization.getMaxNorthing()
                ), visualization.getFirstTimestep(), visualization.getLastTimestep(), visualization.getTimestepSize(),
                visualization.getProgress()
        );
    }
}
