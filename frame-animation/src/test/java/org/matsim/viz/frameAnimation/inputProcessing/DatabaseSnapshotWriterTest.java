package org.matsim.viz.frameAnimation.inputProcessing;

import io.dropwizard.testing.junit.DAOTestRule;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.viz.frameAnimation.persistenceModel.MatsimNetwork;
import org.matsim.viz.frameAnimation.persistenceModel.Snapshot;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DatabaseSnapshotWriterTest {

    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
            .addEntityClass(MatsimNetwork.class)
            .addEntityClass(Visualization.class)
            .addEntityClass(Snapshot.class)
            .setShowSql(true)
            .build();

    private Visualization visualization;
    private DatabaseSnapshotWriter snapshotWriter;

    @Before
    public void setUp() {
        this.visualization = database.inTransaction(() -> {
            val toPersist = new Visualization();
            database.getSessionFactory().getCurrentSession().save(toPersist);
            return toPersist;
        });
        this.snapshotWriter = new DatabaseSnapshotWriter(visualization, database.getSessionFactory());
    }

    @Test
    public void writeOneSnapshot() {

        val agentInfo = TestUtils.createAgentSnapshotInfo(1L, 10, 12);
        final double timestep = 1.0;
        snapshotWriter.beginSnapshot(timestep);
        snapshotWriter.addAgent(agentInfo);
        snapshotWriter.endSnapshot();
        snapshotWriter.finish();

        val viz = database.getSessionFactory().getCurrentSession().find(Visualization.class, visualization.getId());

        assertEquals(1, viz.getSnapshots().size());
        val snapshot = viz.getSnapshots().get(0);
        assertEquals(timestep, snapshot.getTimestep(), 0.001);
        assertEquals(viz, snapshot.getVisualization());

        // the snapshot data should include time, sizeOfPositions, x, y, id
        // 5 * 4byte [floats are 32 bit which is 4 bytes] should be 20
        assertEquals(20, snapshot.getData().length);
        val buffer = ByteBuffer.wrap(snapshot.getData());
        buffer.order(ByteOrder.BIG_ENDIAN);
        // timestep
        assertEquals(timestep, buffer.getFloat(), 0.001);
        // size of values array
        assertEquals(3, buffer.getFloat(), 0.001);
        // x
        assertEquals(agentInfo.getEasting(), buffer.getFloat(), 0.001);
        // y
        assertEquals(agentInfo.getNorthing(), buffer.getFloat(), 0.001);
        // id //TODO fix, when id mapping is in place
        assertEquals(0, buffer.getFloat(), 0.001);

        assertFalse(buffer.hasRemaining());
    }

    @Test
    public void writeMultipleSnapshots() {

        snapshotWriter.beginSnapshot(1);
        TestUtils.createAgentSnapshotInfos(2000000).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.beginSnapshot(2);
        TestUtils.createAgentSnapshotInfos(180000).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.beginSnapshot(3);
        TestUtils.createAgentSnapshotInfos(2700000).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.beginSnapshot(4);
        TestUtils.createAgentSnapshotInfos(6800000).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.finish();

        val viz = database.getSessionFactory().getCurrentSession().find(Visualization.class, visualization.getId());

        assertEquals(4, viz.getSnapshots().size());
    }
}
