package org.matsim.viz.postprocessing.eventAnimation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class ConfigurationResponse {

	private Visualization.Progress progress;
    private double firstTimestep;
    private double lastTimestep;
    private double left;
    private double right;
    private double top;
    private double bottom;

    static ConfigurationResponse createFromVisualization(Visualization visualization) {
        return new ConfigurationResponse(
				visualization.getProgress(),
                visualization.getFirstTimestep(), visualization.getLastTimestep(),
                visualization.getMinEasting(), visualization.getMaxEasting(),
                visualization.getMinNorthing(), visualization.getMaxNorthing()
        );
    }
}
