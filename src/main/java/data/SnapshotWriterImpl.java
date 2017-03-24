package data;

import contracts.SnapshotContract;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.SnapshotWriter;

public class SnapshotWriterImpl implements SnapshotWriter {

    private SimulationData simulationData;
    private SnapshotContract currentSnapshot;

    public SnapshotWriterImpl(double timestepSize) {
        this.simulationData = new SimulationData(timestepSize);
    }

    @Override
    public void beginSnapshot(double v) {

        currentSnapshot = new SnapshotContract(v);
    }

    @Override
    public void endSnapshot() {
        try {
            simulationData.addSnapshot(currentSnapshot);
        } catch (Exception e) {
            System.out.println("Exception in SnapshotWriterImpl:21! Message: " + e.getMessage());
        }
    }

    @Override
    public void addAgent(AgentSnapshotInfo agentSnapshotInfo) {
        currentSnapshot.add(agentSnapshotInfo);
    }

    @Override
    public void finish() {

    }

    public SimulationData getSimulationData() {
        return simulationData;
    }
}
