package data;

import contracts.RectContract;
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
        initializeNetwork(networkFilePath);
        initializeAgents(eventsFilePath, networkFilePath, snapshotPeriod);
        initializePopulation(populationFilePath, networkFilePath);
    }

    private void initializeNetwork(String filePath) {
        networkData = MatsimDataReader.readNetworkFile(filePath);
    }

    private void initializeAgents(String eventsFilePath, String networkFilePath, double snapshotPeriod) {
        simulationData = MatsimDataReader.readEventsFile(eventsFilePath, networkFilePath, snapshotPeriod);
    }

    private void initializePopulation(String populationFilePath, String networkFilePath) {
        populationData = MatsimDataReader.readPopulationFile(populationFilePath, networkFilePath);
    }

    public byte[] getLinks(QuadTree.Rect bounds) throws IOException {
        return networkData.getLinks(bounds);
    }

    public byte[] getSnapshots(QuadTree.Rect bounds, double fromTimestep, int numberOfTimesteps) throws IOException {
        //This will respect the given bounds later
        return simulationData.getSnapshots(fromTimestep, numberOfTimesteps);
    }

    public Object getPlan(double timestep, int index) {
        Id id = simulationData.getId(timestep, index);
        populationData.getPlan(id);
        return null;
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