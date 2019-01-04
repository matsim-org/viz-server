package org.matsim.viz.frameAnimation.inputProcessing;

import io.dropwizard.testing.junit.DAOTestRule;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.matsim.viz.frameAnimation.persistenceModel.MatsimNetwork;
import org.matsim.viz.frameAnimation.persistenceModel.Snapshot;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import java.nio.file.Paths;

public class VisualizationProcessorTest {

    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
            .addEntityClass(MatsimNetwork.class)
            .addEntityClass(Visualization.class)
            .addEntityClass(Snapshot.class)
            .build();

    private VisualizationProcessor testObject;
    private Visualization visualization;

    @Before
    public void setUp() {
        this.testObject = VisualizationProcessor.builder()
                .events(Paths.get(TestUtils.EVENTS_FILE))
                .network(Paths.get(TestUtils.NETWORK_FILE))
                .snapshotPeriod(2)
                .sessionFactory(database.getSessionFactory())
                .build();
        this.visualization = database.inTransaction(() -> {
            val toPersist = new Visualization();
            database.getSessionFactory().getCurrentSession().save(toPersist);
            return toPersist;
        });
    }

   /* @Test
    public void readNetwork() {
        testObject.readNetwork(visualization);

        val network = testObject.getGeneratedNetwork();
        assertNotNull(network.getId());

        // check whether network was persisted
        val persistedNetwork = database.getSessionFactory().getCurrentSession().find(MatsimNetwork.class, network.getId());

        assertNotNull(persistedNetwork);
        // magic number results from the test data
        assertEquals(192, persistedNetwork.getData().length);
    }

    @Test
    public void readSnapshots() {

        testObject.readEvents(visualization);
    }*/

}
