package org.matsim.viz.postprocessing.eventAnimation;

import io.dropwizard.testing.junit.DAOTestRule;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.viz.postprocessing.bundle.Agent;
import org.matsim.viz.postprocessing.bundle.InputFile;
import org.matsim.viz.postprocessing.bundle.Permission;
import org.matsim.viz.postprocessing.bundle.VisualizationGenerator;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.Assert.*;

public class VisualizationResourceTest {

    private static final String vizId = "test-id";

    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
            .addEntityClass(Visualization.class)
            .addEntityClass(MatsimNetwork.class)
            .addEntityClass(LinkTrip.class)
            .addEntityClass(Agent.class)
            .addEntityClass(Permission.class)
            .setShowSql(true)
            .build();

    @Before
    public void setUp() {
        val generator = new DataGenerator();
        Visualization unsavedViz = generator.createVisualization();
        unsavedViz.setId(vizId);

        Visualization viz = database.inTransaction(() -> (Visualization) database.getSessionFactory().getCurrentSession().merge(unsavedViz));

        val inputFiles = new HashMap<String, InputFile>();
        inputFiles.put("network", new InputFile("network", getResourcePath("test-network.xml")));
        inputFiles.put("events", new InputFile("events", getResourcePath("test-events-100.xml.gz")));
        val input = new VisualizationGenerator.Input<Visualization>(viz, inputFiles, new HashMap<>(), database.getSessionFactory());

        generator.generate(input);
    }

    @Test
    public void configuration() {

        val resource = new VisualizationResource(database.getSessionFactory());
        val publicAgent = new Agent("allUsers");

        database.inTransaction(() -> {

            val result = resource.configuration(publicAgent, vizId);
            assertEquals(25200, result.getFirstTimestep(), 0.0001);
            assertEquals(25441, result.getLastTimestep(), 0.0001);
            assertEquals(-2500, result.getLeft(), 0.0001);
            assertEquals(1000, result.getRight(), 0.0001);
            assertEquals(-1000, result.getTop(), 0.0001);
            assertEquals(400, result.getBottom(), 0.0001);
        });
    }

    @Test
    public void network() {

        val resource = new VisualizationResource(database.getSessionFactory());
        val publicAgent = new Agent("allUsers");

        database.inTransaction(() -> {
            val result = resource.network(publicAgent, vizId);

            // not so smart but maybe sufficient?
            assertNotNull(result);
            assertTrue(result.length > 0);
        });
    }

    @Test
    public void linkTrips() {

        val resource = new VisualizationResource(database.getSessionFactory());
        val publicAgent = new Agent("allUsers");

        val result = resource.linkTrips(publicAgent, vizId, 25200, 25441);

        assertEquals(600, result.size());
    }

    @Test
    public void linkTrips_withTimeWindow() {

        val resource = new VisualizationResource(database.getSessionFactory());
        val publicAgent = new Agent("allUsers");

        val result = resource.linkTrips(publicAgent, vizId, 25210, 25290);

        assertEquals(251, result.size());

    }

    private Path getResourcePath(String filename) {
        try {
            return Paths.get(this.getClass().getClassLoader().getResource(filename).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
