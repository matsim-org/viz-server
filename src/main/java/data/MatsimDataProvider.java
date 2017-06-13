package data;

import contracts.RectContract;
import contracts.geoJSON.FeatureCollection;
import org.matsim.api.core.v01.Id;
import org.matsim.core.utils.collections.QuadTree;

import java.io.IOException;

public class MatsimDataProvider {

    private double snapshotPeriod;
    private NetworkData networkData;
    private SnapshotData simulationData;
    private PopulationData populationData;

    public MatsimDataProvider(String networkFilePath, String eventsFilePath, String populationFilePath, double snapshotPeriod) {

        this.snapshotPeriod = snapshotPeriod;

        MatsimDataReader reader = new MatsimDataReader(networkFilePath, eventsFilePath, populationFilePath);
        reader.readAllFiles(snapshotPeriod);

        networkData = reader.getNetworkData();
        simulationData = reader.getSnapshotData();
        populationData = reader.getPopulationData();
    }

    public byte[] getLinks(QuadTree.Rect bounds) throws IOException {
        return networkData.getLinks(bounds);
    }

    public byte[] getSnapshots(QuadTree.Rect bounds, double fromTimestep, int numberOfTimesteps) throws IOException {
        //This will respect the given bounds later
        return simulationData.getSnapshots(fromTimestep, numberOfTimesteps);
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