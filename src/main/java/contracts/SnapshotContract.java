package contracts;

import org.matsim.api.core.v01.Id;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class SnapshotContract {

    private double time;
    private List<AgentSnapshotContract> positions = new ArrayList<>();
    private List<Id> ids = new ArrayList<>();
    private byte[] encodedSnapshot;

    public SnapshotContract(double time) {

        this.time = roundFourDecimals(time);
    }

    public double getTime() {
        return time;
    }

    public void add(AgentSnapshotInfo info) {
        ids.add(info.getId());
        positions.add(new AgentSnapshotContract(info));
    }

    /**
     * Encodes the Snapshot into a byteArray which is accessable via getEncodedMessage
     * The previously addes positions are deleted
     */
    public void encodeSnapshot() {
        int valueSize = Float.BYTES;
        int numberOfPositionsValues = positions.size() * 2; // we are sending (x,y) coordinates
        ByteBuffer buffer = ByteBuffer.allocate(valueSize + valueSize + valueSize * numberOfPositionsValues);
        buffer.order(ByteOrder.BIG_ENDIAN);

        //put the timestamp
        buffer.putFloat((float) this.getTime());

        //put the length of the positionsarray
        buffer.putFloat(numberOfPositionsValues);

        //put positions as x,y,z
        for (AgentSnapshotContract pos : positions) {
            buffer.putFloat((float) pos.getX());
            buffer.putFloat((float) pos.getY());
        }
        encodedSnapshot = buffer.array();
        positions.clear();
        positions = null;
    }

    /**
     * Retreives the snapshot as encoded ByteArray. Make sure to call 'encodeSnaphot' before calling this method
     * @return the snapshot as ByteArray
     */
    public byte[] getEncodedMessage() {
        return encodedSnapshot;
    }

    public Id getIdForIndex(int index) {
        return ids.get(index);
    }

    /**
     * This method is for unittesting
     */
    public List<AgentSnapshotContract> getAgentContracts() {

        return this.positions;
    }

    private double roundFourDecimals(double value) {
        return (double) Math.round(value * 10000) / 10000;
    }
}
