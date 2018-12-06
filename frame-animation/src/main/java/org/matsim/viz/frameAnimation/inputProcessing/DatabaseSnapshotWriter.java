package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hibernate.SessionFactory;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.SnapshotWriter;
import org.matsim.viz.frameAnimation.contracts.SnapshotPosition;
import org.matsim.viz.frameAnimation.persistenceModel.Snapshot;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class DatabaseSnapshotWriter implements SnapshotWriter {

    private final SessionFactory sessionFactory;
    private final Visualization visualization;

    private TempSnapshot currentSnapshot;

    @Override
    public void beginSnapshot(double timestep) {
        this.currentSnapshot = new TempSnapshot(timestep);
    }

    @Override
    public void endSnapshot() {

        val snapshot = new Snapshot();
        visualization.addSnapshot(snapshot);
        snapshot.setTimestep(this.currentSnapshot.timestep);
        snapshot.setData(this.currentSnapshot.encodePositions());

        val session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(snapshot);
        session.getTransaction().commit();
    }

    @Override
    public void addAgent(AgentSnapshotInfo agentSnapshotInfo) {

        if (isOnRoute(agentSnapshotInfo)) {
            this.currentSnapshot.addAgentInfo(agentSnapshotInfo, 0); //TODO calculate real idIndex
        }
    }

    @Override
    public void finish() {

    }

    private boolean isOnRoute(AgentSnapshotInfo info) {
        return info.getAgentState() != AgentSnapshotInfo.AgentState.PERSON_AT_ACTIVITY;
    }

    @RequiredArgsConstructor
    private static class TempSnapshot {

        private final double timestep;
        private List<SnapshotPosition> positions = new ArrayList<>();

        private void addAgentInfo(AgentSnapshotInfo info, int idIndex) {
            this.positions.add(new SnapshotPosition(info, idIndex));
        }

        private byte[] encodePositions() {
            positions.sort(Comparator.comparingInt(SnapshotPosition::getIdIndex));

            int valueSize = Float.BYTES;
            int numberOfPositionsValues = positions.size() * 3; // we are sending (x,y) coordinates and and idIndex
            ByteBuffer buffer = ByteBuffer.allocate(valueSize + valueSize + valueSize * numberOfPositionsValues);
            buffer.order(ByteOrder.BIG_ENDIAN);

            //put the timestamp
            buffer.putFloat((float) this.timestep);

            //put the length of the positionsarray
            buffer.putFloat(numberOfPositionsValues);

            //put positions as x,y and idIndex
            for (SnapshotPosition pos : positions) {
                buffer.putFloat((float) pos.getX());
                buffer.putFloat((float) pos.getY());
                buffer.putFloat((float) pos.getIdIndex());
            }

            return buffer.array();
        }
    }
}
