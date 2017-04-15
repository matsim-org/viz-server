package data;

import org.matsim.core.utils.collections.QuadTree;
import org.matsim.webvis.contracts.Contracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class MatsimDataProvider {

    private double snapshotPeriod;
    private QuadTree<Contracts.Link> networkData;
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

    public Collection<Contracts.Link> getLinks(QuadTree.Rect bounds) {
        ArrayList<Contracts.Link> result = new ArrayList<>();
        networkData.getRectangle(bounds, result);
        return result;
    }

    public byte[] getSnapshots(QuadTree.Rect bounds, double fromTimestep, int numberOfTimesteps) throws IOException {
        //This will respect the given bounds later
        return simulationData.getSnapshots(fromTimestep, numberOfTimesteps);
    }

    public Contracts.Rect getBounds() {

        Contracts.Rect bounds = Contracts.Rect.newBuilder()
                .setLeft(networkData.getMinEasting())
                .setRight(networkData.getMaxEasting())
                .setTop(networkData.getMaxNorthing())
                .setBottom(networkData.getMinNorthing())
                .build();
        return bounds;
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