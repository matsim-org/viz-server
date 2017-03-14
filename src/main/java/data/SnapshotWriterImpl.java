package data;

import contracts.SnapshotContract;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.SnapshotWriter;

import java.util.ArrayList;
import java.util.List;

public class SnapshotWriterImpl implements SnapshotWriter {

    private List<SnapshotContract> snapshots = new ArrayList<>();
    private SnapshotContract currentSnapshot;

    @Override
    public void beginSnapshot(double v) {
        currentSnapshot = new SnapshotContract(v);
    }

    @Override
    public void endSnapshot() {
        snapshots.add(currentSnapshot);
    }

    @Override
    public void addAgent(AgentSnapshotInfo agentSnapshotInfo) {
        currentSnapshot.add(agentSnapshotInfo);
    }

    @Override
    public void finish() {

    }

    public List<SnapshotContract> getSnapshots() {
        return snapshots;
    }
}
