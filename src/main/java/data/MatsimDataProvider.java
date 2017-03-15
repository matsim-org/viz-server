package data;

import contracts.RectContract;
import contracts.SnapshotContract;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;

import java.util.ArrayList;
import java.util.Collection;

public class MatsimDataProvider {

    private final double snapshotPeriod = 1;
    private QuadTree<Link> networkData;
    private SimulationData simulationData;

    public MatsimDataProvider(String networkFilePath, String eventsFilePath) {

        initializeNetwork(networkFilePath);
        initializeAgents(eventsFilePath, networkFilePath);
    }

    private void initializeNetwork(String filePath) {
        networkData = MatsimDataReader.readNetworkFile(filePath);
    }

    private void initializeAgents(String eventsFilePath, String networkFilePath) {
        simulationData = MatsimDataReader.readEventsFile(eventsFilePath, networkFilePath, snapshotPeriod);
    }

    public Collection<Link> getLinks(QuadTree.Rect bounds) {
        ArrayList<Link> result = new ArrayList<>();
        networkData.getRectangle(bounds, result);
        return result;
    }

    public SnapshotContract getSnapshot(QuadTree.Rect bounds, double time) {
        //This will be more sophisticated later
        SnapshotContract snapshot = simulationData.getSnapshot(time);
        if (snapshot == null) {
            snapshot = simulationData.getSnapshot(simulationData.getFirstTimestep());
        }
        return snapshot;
    }

    public RectContract getBounds() {
        return new RectContract(
                networkData.getMinEasting(),
                networkData.getMaxEasting(),
                networkData.getMaxNorthing(),
                networkData.getMinNorthing());
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
