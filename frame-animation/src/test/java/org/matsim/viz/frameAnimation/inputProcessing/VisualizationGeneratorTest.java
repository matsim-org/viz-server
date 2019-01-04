package org.matsim.viz.frameAnimation.inputProcessing;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.frameAnimation.communication.FilesAPI;
import org.matsim.viz.frameAnimation.entities.*;
import org.matsim.viz.frameAnimation.persistenceModel.QVisualization;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VisualizationGeneratorTest extends DatabaseTest {

    private Visualization testViz;

    @Before
    public void setUp() {

        testViz = createVisualization();
        testViz.setId(UUID.randomUUID().toString());
    }

    @Test
    public void generate_downloadFails_stateFailed() {

        val filesAPI = mock(FilesAPI.class);
        when(filesAPI.fetchFile(any(), any())).thenThrow(new RuntimeException());

        val testObject = new VisualizationGenerator(filesAPI, Paths.get("./"), testViz, database.getSessionFactory());

        testObject.generate();

        try (val session = database.getSessionFactory().openSession()) {
            val visualizationTable = QVisualization.visualization;
            val generatedViz = new JPAQueryFactory(session).selectFrom(visualizationTable)
                    .where(visualizationTable.filesServerId.eq(testViz.getId()))
                    .fetchFirst();

            assertEquals(org.matsim.viz.frameAnimation.persistenceModel.Visualization.Progress.Failed, generatedViz.getProgress());
        }
    }

    @Test(expected = InvalidInputException.class)
    public void generate_invalidInput_exception() {

        val filesAPI = mock(FilesAPI.class);
        val invalidInput = new Visualization();
        val testObject = new VisualizationGenerator(filesAPI, Paths.get("./"), invalidInput, database.getSessionFactory());

        testObject.generate();

        fail("invalid input should cause exception");
    }

    @Test
    public void generate_allGood() throws IOException {

        val filesAPI = mock(FilesAPI.class);
        when(filesAPI.fetchFile(any(), eq("events-id"))).thenReturn(Files.newInputStream(Paths.get(TestUtils.EVENTS_FILE_PATH)));
        when(filesAPI.fetchFile(any(), eq("network-id"))).thenReturn(Files.newInputStream(Paths.get(TestUtils.NETWORK_FILE_PATH)));
        when(filesAPI.fetchFile(any(), eq("plans-id"))).thenReturn(Files.newInputStream(Paths.get(TestUtils.POPULATION_FILE_PATH)));

        val testObject = new VisualizationGenerator(
                filesAPI, Paths.get("./"), testViz, database.getSessionFactory()
        );

        testObject.generate();

        try (val session = database.getSessionFactory().openSession()) {
            val vizTable = QVisualization.visualization;
            val generatedViz = new JPAQueryFactory(session).selectFrom(vizTable)
                    .where(vizTable.filesServerId.eq(testViz.getId()))
                    .fetchFirst();

            assertEquals(org.matsim.viz.frameAnimation.persistenceModel.Visualization.Progress.Done, generatedViz.getProgress());
            // do we need more assertions?
        }
    }

    private Visualization createVisualization() {

        Map<String, VisualizationInput> input = new HashMap<>();
        FileEntry events = new FileEntry();
        events.setUserFileName(TestUtils.EVENTS_FILE);
        events.setId("events-id");
        input.put("events", new VisualizationInput("events", events));

        FileEntry network = new FileEntry();
        network.setUserFileName(TestUtils.NETWORK_FILE);
        network.setId("network-id");
        input.put("network", new VisualizationInput("network", network));

        FileEntry plans = new FileEntry();
        plans.setUserFileName(TestUtils.POPULATION_FILE);
        plans.setId("plans-id");
        input.put("plans", new VisualizationInput("plans", plans));

        Map<String, VisualizationParameter> params = new HashMap<>();
        params.put("snapshotInterval", new VisualizationParameter("snapshotInterval", "10"));

        Project project = new Project("name");
        project.setId("project-id");

        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.createFromAuthId("some-auth-id"));
        permissions.add(Permission.createFromAuthId("some-other-id"));

        return new Visualization(
                project, permissions, input, params);
    }
}
