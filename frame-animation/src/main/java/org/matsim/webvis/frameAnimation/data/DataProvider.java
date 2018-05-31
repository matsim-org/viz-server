package org.matsim.webvis.frameAnimation.data;

import org.matsim.webvis.frameAnimation.entities.Visualization;

import java.util.HashMap;
import java.util.Map;

public class DataProvider {

    public final DataProvider Instance = new DataProvider();
    private DataProvider() {}

    private Map<Visualization, SimulationData> visualizations = new HashMap<>();

    public void createVisualization(String visualizationId) {

        //1. fetch viz data from file server

        //2. create simulation data

        //3. store visualization and simulation data in map
    }

    public void getVisualization(String visualizationId) {

        //1. get the viz from the map

        // return sim data
    }

    public void removeViz(String visualizationId) {

        // well, remove visualization and all the data.
    }
}
