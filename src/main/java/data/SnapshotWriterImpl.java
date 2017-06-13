package data;

import contracts.SnapshotContract;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.SnapshotWriter;

import java.io.IOException;

public class SnapshotWriterImpl implements SnapshotWriter {

    private SnapshotContract currentSnapshot;
    private SnapshotData simData;

    SnapshotWriterImpl(double timestepSize) {
        simData = new SnapshotData(timestepSize);
    }

    @Override
    public void beginSnapshot(double v) {
        currentSnapshot = new SnapshotContract(v);
    }

    @Override
    public void endSnapshot() {
        try {
            simData.addSnapshot(currentSnapshot);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addAgent(AgentSnapshotInfo agentSnapshotInfo) {

        if (agentSnapshotInfo.getAgentState() != AgentSnapshotInfo.AgentState.PERSON_AT_ACTIVITY) {
            int idIndex = simData.addId(agentSnapshotInfo.getId());
            currentSnapshot.add(agentSnapshotInfo, idIndex);
        }
    }

    @Override
    public void finish() {

        currentSnapshot = null;
    }

    SnapshotData getSimulationData() {
        return simData;
    }

    /**
     * This is for unittesting
     */
    SnapshotContract getCurrentSnapshot() {
        return currentSnapshot;
    }
}
