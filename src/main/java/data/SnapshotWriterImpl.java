package data;

import contracts.SnapshotContract;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.SnapshotWriter;

import java.io.IOException;

public class SnapshotWriterImpl implements SnapshotWriter {

    private SnapshotContract currentSnapshot;
    private SimulationDataAsBytes simData;

    public SnapshotWriterImpl(double timestepSize) {
        simData = new SimulationDataAsBytes(timestepSize);
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

        currentSnapshot.add(agentSnapshotInfo);
    }

    @Override
    public void finish() {

        currentSnapshot = null;
    }

    public SimulationDataAsBytes getSimulationData() {

        return simData;
    }

    /**
     * This is for unittesting
     */
    SnapshotContract getCurrentSnapshot() {
        return currentSnapshot;
    }
}
