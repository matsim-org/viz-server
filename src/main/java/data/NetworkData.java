package data;

import contracts.RectContract;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.utils.collections.QuadTree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class NetworkData {

    private byte[] networkBuffer;

    private double minEasting = Double.POSITIVE_INFINITY;
    private double maxEasting = Double.NEGATIVE_INFINITY;
    private double minNorthing = Double.POSITIVE_INFINITY;
    private double maxNorthing = Double.NEGATIVE_INFINITY;

    public NetworkData() {
    }

    private static void putLink(ByteBuffer buffer, Link link) {
        buffer.putFloat((float) link.getFromNode().getCoord().getX());
        buffer.putFloat((float) link.getFromNode().getCoord().getY());

        buffer.putFloat((float) link.getToNode().getCoord().getX());
        buffer.putFloat((float) link.getToNode().getCoord().getY());
    }

    public void addNetwork(Network network) {

        int size = network.getLinks().values().size();
        int valueSize = Float.BYTES;
        int numbreOfPositionValues = 4;
        ByteBuffer buffer = ByteBuffer.allocate(valueSize * numbreOfPositionValues * size);
        buffer.order(ByteOrder.BIG_ENDIAN);

        for (Link link : network.getLinks().values()) {
            putLink(buffer, link);
            adjustBoundingRectangle(link);
        }
        networkBuffer = buffer.array();
    }

    private void adjustBoundingRectangle(Link link) {
        adjustBoundingRectangle(link.getFromNode());
        adjustBoundingRectangle(link.getToNode());
    }

    private void adjustBoundingRectangle(Node node) {
        minEasting = Math.min(minEasting, node.getCoord().getX());
        maxEasting = Math.max(maxEasting, node.getCoord().getX());
        minNorthing = Math.min(minNorthing, node.getCoord().getY());
        maxNorthing = Math.max(maxNorthing, node.getCoord().getY());
    }

    public byte[] getLinks() {
        return networkBuffer;
    }

    public RectContract getBounds() {

        return new RectContract(
                minEasting, maxEasting, maxNorthing, minNorthing
        );
    }
}
