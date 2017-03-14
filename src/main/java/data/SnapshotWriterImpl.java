package data;

import contracts.SnapshotContract;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.SnapshotWriter;

import java.util.HashMap;

public class SnapshotWriterImpl implements SnapshotWriter {

    private HashMap<Double, SnapshotContract> snapshots = new HashMap<>();
    private SnapshotContract currentSnapshot;

    @Override
    public void beginSnapshot(double v) {
        currentSnapshot = new SnapshotContract(v);
    }

    @Override
    public void endSnapshot() {
        snapshots.put(currentSnapshot.getTime(), currentSnapshot);
    }

    @Override
    public void addAgent(AgentSnapshotInfo agentSnapshotInfo) {
        currentSnapshot.add(agentSnapshotInfo);
    }

    @Override
    public void finish() {

    }

    public HashMap<Double, SnapshotContract> getSnapshots() {
        return snapshots;
    }
}
