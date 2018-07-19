package org.matsim.webvis.frameAnimation.data;

import org.geojson.FeatureCollection;
import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.error.InvalidInputException;
import org.matsim.webvis.frameAnimation.contracts.ConfigurationResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataProvider {

    public static DataProvider Instance = new DataProvider();

    private static Map<String, VisualizationData> data = new HashMap<>();

    public void add(String vizId, VisualizationData visualizationData) {
        data.put(vizId, visualizationData);
    }

    public byte[] getLinks(String vizId) {
        return find(vizId).getLinks();
    }

    public byte[] getSnapshots(String vizId, double fromTimestamp, int numberOfTimesteps, double speedFactor) throws IOException {
        return find(vizId).getSnapshots(null, fromTimestamp, numberOfTimesteps, speedFactor);
    }

    public FeatureCollection getPlan(String vizId, int idIndex) {
        return find(vizId).getPlan(idIndex);
    }

    public ConfigurationResponse getConfiguration(String vizId) {

        if (!data.containsKey(vizId)) {
            DataController.Instance.fetchVisualizations();
            throw new InvalidInputException("Viz id: " + vizId + " is not in data set");
        }


        VisualizationData viz = data.get(vizId);

        if (!viz.isDone())
            return new ConfigurationResponse(viz.getProgress());
        else
            return new ConfigurationResponse(viz.getSimulationData().getBounds(),
                    viz.getSimulationData().getFirstTimestep(),
                    viz.getSimulationData().getLastTimestep(),
                    viz.getSimulationData().getTimestepSize(),
                    viz.getProgress()
            );
    }

    VisualizationData remove(String vizId) {
        return data.remove(vizId);
    }

    private SimulationData find(String vizId) {

        if (!data.containsKey(vizId)) {
            throw new InvalidInputException("Viz id: " + vizId + " is not in data set");
        }
        if (!data.get(vizId).isDone())
            throw new InternalException("visualization is not ready yet");
        return data.get(vizId).getSimulationData();
    }
}
