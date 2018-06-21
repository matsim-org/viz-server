package org.matsim.webvis.frameAnimation.data;

import org.matsim.api.core.v01.Id;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.webvis.frameAnimation.contracts.RectContract;
import org.matsim.webvis.frameAnimation.contracts.geoJSON.FeatureCollection;

import java.io.IOException;

public class SimulationData {

    private double snapshotPeriod;
    private NetworkData networkData;
    private SnapshotData simulationData;
    private PopulationData populationData;

    public SimulationData(String networkFilePath, String eventsFilePath, String populationFilePath, double snapshotPeriod) {

        this.snapshotPeriod = snapshotPeriod;

        MatsimDataReader reader = new MatsimDataReader(networkFilePath, eventsFilePath, populationFilePath);
        reader.readAllFiles(snapshotPeriod);

        networkData = reader.getNetworkData();
        simulationData = reader.getSnapshotData();
        populationData = reader.getPopulationData();
    }

    public byte[] getLinks() {
        return networkData.getLinks();
    }

    public byte[] getSnapshots(QuadTree.Rect bounds, double fromTimestep, int numberOfTimesteps, double speedFactor)
            throws IOException {
        //This will respect the given bounds later
        return simulationData.getSnapshots(fromTimestep, numberOfTimesteps, speedFactor);
    }

    public FeatureCollection getPlan(int idIndex) {
        Id id = simulationData.getId(idIndex);
        return populationData.getSelectedPlan(id);
    }

    public RectContract getBounds() {
        return networkData.getBounds();
    }

    public double getTimestepSize() {
        return snapshotPeriod;
    }

    public double getFirstTimestep() {
        return simulationData.getFirstTimestep();
    }

    public double getLastTimestep() {
        return simulationData.getLastTimestep();
    }
}