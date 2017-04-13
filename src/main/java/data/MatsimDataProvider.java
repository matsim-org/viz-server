package data;

import contracts.RectContract;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.webvis.contracts.Contracts;

import java.util.ArrayList;
import java.util.Collection;

public class MatsimDataProvider {

    private double snapshotPeriod;
    private QuadTree<Link> networkData;
    private Contracts.SimulationData simulationData;

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

    public Collection<Link> getLinks(QuadTree.Rect bounds) {
        ArrayList<Link> result = new ArrayList<>();
        networkData.getRectangle(bounds, result);
        return result;
    }

   /* public List<SnapshotContract> getSnapshot(QuadTree.Rect bounds, double startTime, int size) {
        //This will be more sophisticated later
        return simulationData.getSnapshots(startTime, size);
    }*/

    public Contracts.SimulationData getSnapshots() {
        return simulationData;
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