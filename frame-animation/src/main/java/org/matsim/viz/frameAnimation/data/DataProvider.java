package org.matsim.viz.frameAnimation.data;

import org.geojson.FeatureCollection;
import org.matsim.viz.error.ForbiddenException;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.viz.frameAnimation.entities.Permission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataProvider {

    public static DataProvider Instance = new DataProvider();

    DataController dataController = DataController.Instance;
    private Map<String, VisualizationData> data = new HashMap<>();

    public void add(String vizId, VisualizationData visualizationData) {
        data.put(vizId, visualizationData);
    }

    boolean contains(String vizId) {
        return data.containsKey(vizId);
    }

    VisualizationData.Progress getProgress(String vizId) {
        return data.get(vizId).getProgress();
    }

    public void remove(String vizId) {
        data.remove(vizId);
    }

    public byte[] getLinks(String vizId, Permission permission) {
        return find(vizId, permission).getLinks();
    }

    public ByteArrayOutputStream getSnapshots(String vizId, double fromTimestamp, int numberOfTimesteps, double speedFactor
            , Permission permission) throws IOException {
        return find(vizId, permission).getSnapshots(null, fromTimestamp, numberOfTimesteps, speedFactor);
    }

    public FeatureCollection getPlan(String vizId, int idIndex, Permission permission) {
        return find(vizId, permission).getPlan(idIndex);
    }

    public ConfigurationResponse getConfiguration(String vizId, Permission permission) {

        if (!data.containsKey(vizId)) {
            throw new InvalidInputException("Viz id: " + vizId + " is not in data set");
        }

        VisualizationData viz = data.get(vizId);

        if (!viz.getPermissions().contains(permission) && !viz.getPermissions().contains(Permission.getPublicPermission()))
            throw new ForbiddenException("You don't have access to this visualization");

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

    private SimulationData find(String vizId, Permission permission) {

        if (!data.containsKey(vizId)) {
            throw new InvalidInputException("Viz id: " + vizId + " is not in data set");
        }
        VisualizationData vizData = data.get(vizId);

        if (!vizData.getPermissions().contains(permission) && !vizData.getPermissions().contains(Permission.getPublicPermission()))
            throw new ForbiddenException("you don't have access to this visualization");
        if (!vizData.isDone())
            throw new InternalException("visualization is not ready yet");

        return data.get(vizId).getSimulationData();
    }
}
