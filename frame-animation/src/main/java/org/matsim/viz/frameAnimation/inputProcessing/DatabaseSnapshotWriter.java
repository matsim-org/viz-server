package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.SnapshotWriter;
import org.matsim.viz.frameAnimation.contracts.SnapshotPosition;
import org.matsim.viz.frameAnimation.persistenceModel.Snapshot;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Log
public class DatabaseSnapshotWriter implements SnapshotWriter {

    private final Visualization visualization;
    private final EntityManager entityManager;
    @Getter
    private final List<Id<Person>> agentIds = new ArrayList<>();

    private TempSnapshot currentSnapshot;

    DatabaseSnapshotWriter(Visualization visualization, EntityManagerFactory entityManagerFactory) {
        //this.visualizationId = visualization;
        this.visualization = visualization;

        // open a new session which this instance owns
        this.entityManager = entityManagerFactory.createEntityManager();
        this.entityManager.getTransaction().begin();
    }

    @Override
    public void beginSnapshot(double timestep) {

        this.currentSnapshot = new TempSnapshot(timestep);
        setFirstOrLastTimestep(timestep);
    }

    @Override
    public void endSnapshot() {

        val snapshot = new Snapshot();
        snapshot.setTimestep(this.currentSnapshot.timestep);
        snapshot.setData(this.currentSnapshot.encodePositions());
        snapshot.setVisualization(visualization);

        entityManager.persist(snapshot);
        entityManager.flush();
    }

    @Override
    public void addAgent(AgentSnapshotInfo agentSnapshotInfo) {

        if (isOnRoute(agentSnapshotInfo))
            this.currentSnapshot.addAgentInfo(agentSnapshotInfo, getIdIndex(agentSnapshotInfo.getId()));
    }

    @Override
    public void finish() {

        // persist visualization with first and last timestep set
        entityManager.merge(visualization);

        // finish the session!
        entityManager.getTransaction().commit();
        entityManager.close();
        log.info("Finished writing snapshots.");
    }

    private boolean isOnRoute(AgentSnapshotInfo info) {
        return info.getAgentState() != AgentSnapshotInfo.AgentState.PERSON_AT_ACTIVITY;
    }

    private int getIdIndex(Id<Person> id) {
        int index = agentIds.indexOf(id);
        if (index < 0) {
            agentIds.add(id);
            index = agentIds.size() - 1;
        }
        return index;
    }

    private void setFirstOrLastTimestep(double timestep) {
        if (timestep < visualization.getFirstTimestep())
            visualization.setFirstTimestep(timestep);
        if (timestep > visualization.getLastTimestep()) {
            visualization.setLastTimestep(timestep);
        }
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
