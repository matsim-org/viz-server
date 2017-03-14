package data;

import contracts.SnapshotContract;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MatsimDataProvider {

    private QuadTree<Link> networkData;
    private List<SnapshotContract> snapshots;

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
        SnapshotContract snapshot = snapshots.stream().filter(e -> e.getTime() == time).findFirst().get();
        return snapshot;
    }
}
