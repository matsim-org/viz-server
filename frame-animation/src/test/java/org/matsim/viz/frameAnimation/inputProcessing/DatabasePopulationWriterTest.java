package org.matsim.viz.frameAnimation.inputProcessing;

import io.dropwizard.testing.junit.DAOTestRule;
import lombok.val;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Identifiable;
import org.matsim.viz.frameAnimation.persistenceModel.MatsimNetwork;
import org.matsim.viz.frameAnimation.persistenceModel.Plan;
import org.matsim.viz.frameAnimation.persistenceModel.Snapshot;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import java.nio.file.Paths;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class DatabasePopulationWriterTest {

    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
            .addEntityClass(MatsimNetwork.class)
            .addEntityClass(Visualization.class)
            .addEntityClass(Snapshot.class)
            .addEntityClass(Plan.class)
            .setShowSql(true)
            .build();

    @Test
    public void writeData() {

        Visualization visualization = database.inTransaction(() -> {
            val toPersist = new Visualization();
            database.getSessionFactory().getCurrentSession().save(toPersist);
            return toPersist;
        });
        val network = TestUtils.loadTestNetwork();
        val population = TestUtils.loadTestPopulation(network);

        val idMapping = population.getPersons().values().stream().map(Identifiable::getId).collect(Collectors.toList());
        DatabasePopulationWriter writer = new DatabasePopulationWriter(Paths.get(TestUtils.POPULATION_FILE_PATH), network,
                idMapping, database.getSessionFactory(), visualization);
        writer.readPopulationAndWriteToDatabase();

        // open a new session to force a reload of the visualization from the db
        try (val session = database.getSessionFactory().openSession()) {
            val viz = session.find(Visualization.class, visualization.getId());
            assertEquals(100, viz.getPlans().size());
            viz.getPlans().forEach(plan -> assertTrue(plan.getIdIndex() > -1));
        }
    }
}
