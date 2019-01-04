package org.matsim.viz.frameAnimation.inputProcessing;

import io.dropwizard.testing.junit.DAOTestRule;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.viz.frameAnimation.persistenceModel.MatsimNetwork;
import org.matsim.viz.frameAnimation.persistenceModel.Plan;
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
            .addEntityClass(Plan.class)
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

 /*   @Test
    public void dbTest() {

        EntityManager em = database.getSessionFactory().openSession();
        em.getTransaction().begin();

        Visualization viz = new Visualization();

        em.persist(viz);

        em.getTransaction().commit();
        em.close();
        String vizId = viz.getId();

        EntityManager otherEm = database.getSessionFactory().openSession();
        otherEm.getTransaction().begin();

        Visualization fetchedViz = otherEm.getReference(Visualization.class, vizId);
        Snapshot snapshot = new Snapshot();
        snapshot.setVisualization(fetchedViz);

        otherEm.persist(snapshot);
        otherEm.getTransaction().commit();

        EntityManager thirdEm = database.getSessionFactory().openSession();

        Visualization thirdViz = thirdEm.find(Visualization.class, vizId);

        thirdEm.close();
        otherEm.close();
        assertNotNull(thirdViz);
    }
    */

    @Test
    public void writeOneSnapshot() {

        val agentInfo = TestUtils.createAgentSnapshotInfo(1L, 10, 12);
        final double timestep = 1.0;
        snapshotWriter.beginSnapshot(timestep);
        snapshotWriter.addAgent(agentInfo);
        snapshotWriter.endSnapshot();
        snapshotWriter.finish();

        Visualization fetchedVisualization;
        Snapshot expectedSnapshot;

        // we need a new session to force a reload from the database
        try (val session = database.getSessionFactory().openSession()) {
            fetchedVisualization = session.find(Visualization.class, visualization.getId());
            assertEquals(1, fetchedVisualization.getSnapshots().size());
            expectedSnapshot = fetchedVisualization.getSnapshots().get(0);
        }

        assertEquals(timestep, expectedSnapshot.getTimestep(), 0.001);
        assertEquals(fetchedVisualization, expectedSnapshot.getVisualization());

        // the snapshot data should include time, sizeOfPositions, x, y, id
        // 5 * 4byte [floats are 32 bit which is 4 bytes] should be 20
        assertEquals(20, expectedSnapshot.getData().length);
        val buffer = ByteBuffer.wrap(expectedSnapshot.getData());
        buffer.order(ByteOrder.BIG_ENDIAN);
        // timestep
        assertEquals(timestep, buffer.getFloat(), 0.001);
        // size of values array
        assertEquals(3, buffer.getFloat(), 0.001);
        // x
        assertEquals(agentInfo.getEasting(), buffer.getFloat(), 0.001);
        // y
        assertEquals(agentInfo.getNorthing(), buffer.getFloat(), 0.001);
        // id should be 0, when there is only one agent
        assertEquals(0, buffer.getFloat(), 0.001);

        assertFalse(buffer.hasRemaining());
    }

    @Test
    public void writeMultipleSnapshots() {

        snapshotWriter.beginSnapshot(1);
        TestUtils.createAgentSnapshotInfos(20000).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.beginSnapshot(2);
        TestUtils.createAgentSnapshotInfos(1800).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.beginSnapshot(3);
        TestUtils.createAgentSnapshotInfos(27000).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.beginSnapshot(4);
        TestUtils.createAgentSnapshotInfos(68000).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.finish();

        // get a new session to force a reload from the database
        try (val session = database.getSessionFactory().openSession()) {
            val viz = session.find(Visualization.class, visualization.getId());
            assertEquals(4, viz.getSnapshots().size());
        }
    }
}
