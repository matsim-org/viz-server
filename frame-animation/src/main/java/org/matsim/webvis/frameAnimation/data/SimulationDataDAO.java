package org.matsim.webvis.frameAnimation.data;

import org.matsim.webvis.error.InvalidInputException;
import org.matsim.webvis.frameAnimation.contracts.RectContract;
import org.matsim.webvis.frameAnimation.contracts.geoJSON.FeatureCollection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimulationDataDAO {

    public static SimulationDataDAO Instance = new SimulationDataDAO();

    private static Map<String, SimulationData> data = new HashMap<>();

    public void add(String vizId, SimulationData simulationData) {
        data.put(vizId, simulationData);
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

    public RectContract getBounds(String vizId) {
        return find(vizId).getBounds();
    }

    public double getTimestepSize(String vizId) {
        return find(vizId).getTimestepSize();
    }

    public double getFirstTimestep(String vizId) {
        return find(vizId).getFirstTimestep();
    }

    public double getLastTimestep(String vizId) {
        return find(vizId).getLastTimestep();
    }

    public SimulationData remove(String vizId) {
        return data.remove(vizId);
    }

    private SimulationData find(String vizId) {

        if (!data.containsKey(vizId))
            throw new InvalidInputException("Viz id: " + vizId + " is not in data set");
        return data.get(vizId);
    }
}
