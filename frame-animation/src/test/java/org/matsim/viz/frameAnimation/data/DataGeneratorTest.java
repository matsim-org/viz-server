package org.matsim.viz.frameAnimation.data;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.error.UnauthorizedException;
import org.matsim.viz.frameAnimation.communication.FilesAPI;
import org.matsim.viz.frameAnimation.entities.*;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import java.io.InputStream;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataGeneratorTest {

    private DataGenerator testObject;
    private Visualization testViz;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadConfig();
    }

    @Before
    public void setUp() {
        testViz = createVisualization();
        testViz.setId(UUID.randomUUID().toString());
        testObject = new DataGenerator(testViz);
        testObject.dataProvider = new DataProvider();
    }

    @Test(expected = InvalidInputException.class)
    public void constructor_invalidInput() {

        Visualization invalid = new Visualization();

        new DataGenerator(invalid);
    }

    @Test
    public void generate_dataAlreadyPresent_abort() {

        final Visualization viz = testViz;
        testObject.dataProvider.add(viz.getId(), new VisualizationData());
        testObject.filesAPI = mock(FilesAPI.class);

        testObject.generate();

        verify(testObject.filesAPI, never()).fetchFile(any(), any());
    }

    @Test
    public void generate_downloadException_stateFailed() {

        final Visualization viz = testViz;
        testObject.filesAPI = mock(FilesAPI.class);
        when(testObject.filesAPI.fetchFile(any(), any())).thenThrow(new RuntimeException());

        testObject.generate();

        assertEquals(VisualizationData.Progress.Failed, testObject.dataProvider.getProgress(viz.getId()));
    }

    @Test
    public void generate_downloadUnauthorizedException_removeVizFromDataProvider() {

        final Visualization viz = testViz;
        testObject.filesAPI = mock(FilesAPI.class);
        when(testObject.filesAPI.fetchFile(any(), any())).thenThrow(new UnauthorizedException(""));

        testObject.generate();

        assertFalse(DataProvider.Instance.contains(viz.getId()));
    }

    @Test
    public void generate_allGood() {

        final Visualization viz = testViz;
        SimulationData mockedData = mock(SimulationData.class);
        InputStream stream = mock(InputStream.class);
        testObject.filesAPI = mock(FilesAPI.class);
        testObject.simulationDataFactory = mock(SimulationDataFactory.class);
        when(testObject.simulationDataFactory.createSimulationData(anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(mockedData);
        when(testObject.filesAPI.fetchFile(any(), any())).thenReturn(stream);

        testObject.generate();

        assertTrue(testObject.dataProvider.contains(viz.getId()));
        assertEquals(VisualizationData.Progress.Done, testObject.dataProvider.getProgress(viz.getId()));
    }

    private Visualization createVisualization() {

        Map<String, VisualizationInput> input = new HashMap<>();
        FileEntry entry = new FileEntry();
        entry.setUserFileName("file.txt");
        entry.setId("entry-id");
        input.put("network", new VisualizationInput("network", entry));
        input.put("events", new VisualizationInput("events", entry));
        input.put("plans", new VisualizationInput("plans", entry));

        Map<String, VisualizationParameter> params = new HashMap<>();
        params.put("snapshotInterval", new VisualizationParameter("snapshotInterval", "10"));

        Project project = new Project("name");
        project.setId("project-id");

        Set<Permission> permissions = new HashSet<>();
        permissions.add(new Permission("some-auth-id"));
        permissions.add(new Permission("some-other-id"));

        return new Visualization(
                project, permissions, input, params);
    }
}
