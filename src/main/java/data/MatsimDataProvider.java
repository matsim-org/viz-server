package data;

import org.matsim.core.utils.collections.QuadTree;
import org.matsim.webvis.contracts.Contracts;

import java.io.IOException;

public class MatsimDataProvider {

    private double snapshotPeriod;
    private NetworkData networkData;
    private SimulationDataAsBytes simulationData;

    public MatsimDataProvider(String networkFilePath, String eventsFilePath, double snapshotPeriod) {

        this.snapshotPeriod = snapshotPeriod;
        initializeNetwork(networkFilePath);
        initializeAgents(eventsFilePath, networkFilePath, snapshotPeriod);
    }

    private void initializeNetwork(String filePath) {
        networkData = MatsimDataReader.readNetworkFile(filePath);
    }

    private void initializeAgents(String eventsFilePath, String networkFilePath, double snapshotPeriod) {
        simulationData = MatsimDataReader.readEventsFile(eventsFilePath, networkFilePath, snapshotPeriod);
    }

    public byte[] getLinks(QuadTree.Rect bounds) throws IOException {
        return networkData.getLinks(bounds);
    }

    public byte[] getSnapshots(QuadTree.Rect bounds, double fromTimestep, int numberOfTimesteps) throws IOException {
        //This will respect the given bounds later
        return simulationData.getSnapshots(fromTimestep, numberOfTimesteps);
    }

    public Contracts.Rect getBounds() {
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