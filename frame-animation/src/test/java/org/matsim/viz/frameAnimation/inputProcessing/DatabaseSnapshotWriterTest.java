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

import static org.junit.Assert.assertEquals;

public class DatabaseSnapshotWriterTest {

    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
            .addEntityClass(MatsimNetwork.class)
            .addEntityClass(Visualization.class)
            .addEntityClass(Snapshot.class)
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
        this.snapshotWriter = new DatabaseSnapshotWriter(database.getSessionFactory(), visualization);
    }

    @Test
    public void writeOneSnapshot() {

        snapshotWriter.beginSnapshot(1);
        snapshotWriter.addAgent(TestUtils.createAgentSnapshotInfo(1L, 10, 12));
        snapshotWriter.endSnapshot();
        snapshotWriter.finish();

        val viz = database.getSessionFactory().getCurrentSession().find(Visualization.class, visualization.getId());

        assertEquals(1, viz.getSnapshots().size());
    }

    @Test
    public void writeMultipleSnapshots() {

        snapshotWriter.beginSnapshot(1);
        TestUtils.createAgentSnapshotInfos(20).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.beginSnapshot(2);
        TestUtils.createAgentSnapshotInfos(18).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.beginSnapshot(3);
        TestUtils.createAgentSnapshotInfos(27).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.beginSnapshot(4);
        TestUtils.createAgentSnapshotInfos(68).forEach(info -> snapshotWriter.addAgent(info));
        snapshotWriter.endSnapshot();
        snapshotWriter.finish();

        val viz = database.getSessionFactory().getCurrentSession().find(Visualization.class, visualization.getId());

        assertEquals(4, viz.getSnapshots().size());
    }
}
