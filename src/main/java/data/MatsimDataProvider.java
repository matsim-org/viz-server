package data;

import contracts.RectContract;
import contracts.SnapshotContract;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MatsimDataProvider {

    private QuadTree<Link> networkData;
    private HashMap<Double, SnapshotContract> snapshots;

    public MatsimDataProvider(String networkFilePath, String eventsFilePath) {

        initializeNetwork(networkFilePath);
        initializeAgents(eventsFilePath, networkFilePath);
    }

    private void initializeNetwork(String filePath) {
        networkData = MatsimDataReader.readNetworkFile(filePath);
    }

    private void initializeAgents(String eventsFilePath, String networkFilePath) {
        snapshots = MatsimDataReader.readEventsFile(eventsFilePath, networkFilePath);
    }

    public Collection<Link> getLinks(QuadTree.Rect bounds) {
        ArrayList<Link> result = new ArrayList<>();
        networkData.getRectangle(bounds, result);
        return result;
    }

    public SnapshotContract getSnapshot(QuadTree.Rect bounds, double time) {
        //This will be more sophisticated later
        SnapshotContract snapshot = snapshots.get(time);
        if (snapshot == null) {
            double smallestTimestep = snapshots.keySet().stream().mapToDouble(d -> d).min().getAsDouble();
            snapshot = snapshots.get(smallestTimestep);
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
}
