package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import java.nio.file.Paths;

import static junit.framework.TestCase.assertTrue;

public class VisualizationProcessorTest extends DatabaseTest {

    private VisualizationProcessor testObject;
    private Visualization visualization;

    @Before
    public void setUp() {
        this.visualization = database.inTransaction(() -> {
            val toPersist = new Visualization();
            toPersist.setTimestepSize(10);
            database.getSessionFactory().getCurrentSession().save(toPersist);
            return toPersist;
        });

        this.testObject = VisualizationProcessor.builder()
                .events(Paths.get(TestUtils.EVENTS_FILE))
                .network(Paths.get(TestUtils.NETWORK_FILE))
                .population(Paths.get(TestUtils.POPULATION_FILE))
                .visualization(visualization)
                .emFactory(database.getSessionFactory())
                .build();
    }

    @Test
    public void processVisualization() {

        testObject.processVisualization();

        try (val session = database.getSessionFactory().openSession()) {
            val processedViz = session.find(Visualization.class, visualization.getId());

            assertTrue(processedViz.getPlans().size() > 0);
            assertTrue(processedViz.getMatsimNetwork().getData().length > 0);
            assertTrue(processedViz.getSnapshots().size() > 0);
            assertTrue(processedViz.getFirstTimestep() != 0);
            assertTrue(processedViz.getLastTimestep() != 0);
            assertTrue(processedViz.getTimestepSize() != 0);
            assertTrue(processedViz.getMinNorthing() != 0);
            assertTrue(processedViz.getMaxNorthing() != 0);
            assertTrue(processedViz.getMinEasting() != 0);
            assertTrue(processedViz.getMaxEasting() != 0);
        }
    }
}
