package contracts;

import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class SnapshotContract {

    private double time;
    private List<AgentSnapshotContract> positions = new ArrayList<>();

    public SnapshotContract(double time) {

        this.time = roundFourDecimals(time);
    }

    public List<AgentSnapshotContract> getAgentInformations() {
        return positions;
    }

    public double getTime() {
        return time;
    }

    public void add(AgentSnapshotInfo info) {
        positions.add(new AgentSnapshotContract(info));
    }

    public byte[] toByteArray() {
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
        return buffer.array();
    }

    private double roundFourDecimals(double value) {
        return (double) Math.round(value * 10000) / 10000;
    }
}
