package data;

import contracts.RectContract;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class NetworkData {

    private QuadTree<byte[]> networkAsBytes;

    public NetworkData(QuadTree.Rect bounds) {
        networkAsBytes = new QuadTree<>(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY);
    }

    public void addLink(Link link) {
        int valueSize = Float.BYTES;
        int numberOfPositionValues = 4;
        ByteBuffer buffer = ByteBuffer.allocate(valueSize * numberOfPositionValues);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.putFloat((float) link.getFromNode().getCoord().getX());
        buffer.putFloat((float) link.getFromNode().getCoord().getY());

        buffer.putFloat((float) link.getToNode().getCoord().getX());
        buffer.putFloat((float) link.getToNode().getCoord().getY());

        networkAsBytes.put(link.getCoord().getX(), link.getCoord().getY(), buffer.array());
    }

    public byte[] getLinks(QuadTree.Rect bounds) throws IOException {
        List<byte[]> result = new ArrayList<>();
        networkAsBytes.getRectangle(bounds, result);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        for (byte[] bytes : result) {
            stream.write(bytes);
        }
        return stream.toByteArray();
    }

    public RectContract getBounds() {

        return new RectContract(
                networkAsBytes.getMinEasting(),
                networkAsBytes.getMaxEasting(),
                networkAsBytes.getMaxNorthing(),
                networkAsBytes.getMinNorthing()
        );
    }
}
