package data;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.SnapshotGenerator;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.webvis.contracts.Contracts;

public final class MatsimDataReader {

    private MatsimDataReader() {
    }

    static QuadTree<Contracts.Link> readNetworkFile(String path) {

        Network network = loadNetworkFile(path);
        return initNetworkData(network);
    }

    private static Network loadNetworkFile(String path) {
        Network network = NetworkUtils.createNetwork();
        MatsimNetworkReader reader = new MatsimNetworkReader(network);
        reader.readFile(path);
        return network;
    }

    private static QuadTree<Contracts.Link> initNetworkData(Network network) {

        QuadTree.Rect bounds = calculateBoundingRectangle(network);
        QuadTree<Contracts.Link> links = new QuadTree<>(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY);

        for (final Link link : network.getLinks().values()) {
            Coord center = link.getCoord();

            //put the links as protobuf inside the tree
            Contracts.Link.Builder linkBuilder = Contracts.Link.newBuilder()
                    .setFromX(link.getFromNode().getCoord().getX())
                    .setFromY(link.getFromNode().getCoord().getY())
                    .setToX(link.getToNode().getCoord().getX())
                    .setToY(link.getToNode().getCoord().getY());
            links.put(center.getX(), center.getY(), linkBuilder.build());
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

    public static Contracts.SimulationData readEventsFile(String eventsFilePath,
                                                          String networkFilePath, double snapshotPeriod) {
        Network network = loadNetworkFile(networkFilePath);
        Config config = ConfigUtils.createConfig();
        config.qsim().setSnapshotStyle(QSimConfigGroup.SnapshotStyle.queue);
        SnapshotGenerator generator = new SnapshotGenerator(network, snapshotPeriod, config.qsim());
        SnapshotWriterImpl writer = new SnapshotWriterImpl(snapshotPeriod);
        generator.addSnapshotWriter(writer);
        EventsManager eventsManager = EventsUtils.createEventsManager();
        eventsManager.addHandler(generator);
        MatsimEventsReader reader = new MatsimEventsReader(eventsManager);
        reader.readFile(eventsFilePath);
        generator.finish();
        writer.finish();
        return writer.getSimulationData();
    }
}