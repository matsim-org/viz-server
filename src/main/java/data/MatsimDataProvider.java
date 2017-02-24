package data;

import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;

import java.util.ArrayList;
import java.util.Collection;

public class MatsimDataProvider {

    private QuadTree<Link> networkData;

    public MatsimDataProvider(String networkFilePath, String eventsFilePath) {

        initializeNetwork(networkFilePath);
        initializeAgents(eventsFilePath);
    }

    private void initializeNetwork(String filePath) {
        networkData = MatsimDataReader.readNetworkFile(filePath);
    }

    private void initializeAgents(String eventsFilePath) {
        //not yet implemented
    }

    public Collection<Link> getLinks(QuadTree.Rect bounds) {
        ArrayList<Link> result = new ArrayList<>();
        networkData.getRectangle(bounds, result);
        return result;
    }
}
