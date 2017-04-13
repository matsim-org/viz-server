package data;

import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.SnapshotWriter;
import org.matsim.webvis.contracts.Contracts;

public class SnapshotWriterImpl implements SnapshotWriter {

    private Contracts.SimulationData.Builder simulationDataBuilder;
    private Contracts.Snapshot.Builder currentSnapshotBuilder;

    public SnapshotWriterImpl(double timestepSize) {
        this.simulationDataBuilder = Contracts.SimulationData.newBuilder();
        simulationDataBuilder.setTimestepSize(timestepSize);
        simulationDataBuilder.setFirstTimestep(Double.MAX_VALUE);
        simulationDataBuilder.setLastTimestep(Double.MIN_VALUE);
    }

    @Override
    public void beginSnapshot(double v) {

        currentSnapshotBuilder = Contracts.Snapshot.newBuilder();
        currentSnapshotBuilder.setTime(v);
        setFirstOrLastTimestep(v);
    }

    @Override
    public void endSnapshot() {
        Contracts.Snapshot snapshot = currentSnapshotBuilder.build();
        simulationDataBuilder.addSnapshots(snapshot);
    }

    @Override
    public void addAgent(AgentSnapshotInfo agentSnapshotInfo) {
        Contracts.Position position = Contracts.Position.newBuilder()
                .setAgentId(agentSnapshotInfo.getId().toString())
                .setX(agentSnapshotInfo.getEasting())
                .setY(agentSnapshotInfo.getNorthing()).build();
        currentSnapshotBuilder.addPositions(position);
    }

    @Override
    public void finish() {

    }

    public Contracts.SimulationData getSimulationData() {
        return simulationDataBuilder.build();
    }

    private void setFirstOrLastTimestep(double time) {
        if (time < simulationDataBuilder.getFirstTimestep()) {
            simulationDataBuilder.setFirstTimestep(time);
        }
        if (time > simulationDataBuilder.getLastTimestep()) {
            simulationDataBuilder.setLastTimestep(time);
        }
    }
}
