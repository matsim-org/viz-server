package data;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.collections.QuadTree;

public final class MatsimDataReader {

    private MatsimDataReader() {
    }

    static QuadTree<Link> readNetworkFile(String path) {

        Network network = NetworkUtils.createNetwork();
        MatsimNetworkReader reader = new MatsimNetworkReader(network);
        reader.readFile(path);
        return initNetworkData(network);
    }

    private static QuadTree<Link> initNetworkData(Network network) {

        QuadTree.Rect bounds = calculateBoundingRectangle(network);
        QuadTree<Link> links = new QuadTree<>(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY);

        for (final Link link : network.getLinks().values()) {
            Coord center = link.getCoord();
            links.put(center.getX(), center.getY(), link);
        }
        return links;
    }

    private static QuadTree.Rect calculateBoundingRectangle(Network network) {
        double minEasting = Double.POSITIVE_INFINITY;
        double maxEasting = Double.NEGATIVE_INFINITY;
        double minNorthing = Double.POSITIVE_INFINITY;
        double maxNorthing = Double.NEGATIVE_INFINITY;

        for (Node node : network.getNodes().values()) {
            minEasting = Math.min(minEasting, node.getCoord().getX());
            maxEasting = Math.max(maxEasting, node.getCoord().getX());
            minNorthing = Math.min(minNorthing, node.getCoord().getY());
            maxNorthing = Math.max(maxNorthing, node.getCoord().getY());
        }
        //all nodes should lie within the bounding rectangle
        maxEasting += 1;
        maxNorthing += 1;

        return new QuadTree.Rect(minEasting, minNorthing, maxEasting, maxNorthing);
    }

    public void readEventsFile(String path) {

    }
}
