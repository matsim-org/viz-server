package org.matsim.viz.frameAnimation.data;

import junit.framework.TestCase;
import org.geojson.FeatureCollection;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.viz.error.ForbiddenException;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.viz.frameAnimation.entities.Permission;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;

public class DataProviderTest {

    private DataProvider testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadConfig();
    }

    @Before
    public void setUp() {
        testObject = new DataProvider();
    }

    @Test
    public void add() {

        final String id = "id";
        VisualizationData data = new VisualizationData();

        testObject.add(id, data);

        assertTrue(testObject.contains(id));
    }

    @Test
    public void remove() {

        final String id = "id";
        VisualizationData data = new VisualizationData();
        testObject.add(id, data);

        assertTrue(testObject.contains(id));

        testObject.remove(id);

        assertFalse(testObject.contains(id));
    }

    @Test
    public void contains() {

        final String id = "id";

        assertFalse(testObject.contains(id));

        VisualizationData data = new VisualizationData();
        testObject.add(id, data);

        assertTrue(testObject.contains(id));
    }

    @Test
    public void getProgress() {

        final String id = "id";
        VisualizationData data = new VisualizationData();
        data.setProgress(VisualizationData.Progress.Done);
        testObject.add(id, data);

        VisualizationData.Progress result = testObject.getProgress(id);

        assertEquals(data.getProgress(), result);
    }

    @Test
    public void getLinks() {

        final String id = "asdf";
        final VisualizationData data = createVisualizationData();
        testObject.add(id, data);

        byte[] result = testObject.getLinks(id, Permission.getPublicPermission());

        assertEquals(data.getSimulationData().getLinks(), result);
    }

    @Test(expected = ForbiddenException.class)
    public void getLinks_noPermission_exception() {

        final String id = "asdf";
        final VisualizationData data = createVisualizationData();
        Set<Permission> privatePermissions = new HashSet<>();
        privatePermissions.add(Permission.createFromAuthId("some-id"));
        data.setPermissions(privatePermissions);
        testObject.add(id, data);

        testObject.getLinks(id, Permission.createFromAuthId("other-id"));
    }

    @Test(expected = InvalidInputException.class)
    public void getLinks_notInDataset() {

        testObject.getLinks("invalid-id", Permission.getPublicPermission());
    }

    @Test(expected = InternalException.class)
    public void getLinks_notDoneYet() {

        final String id = "sdaf";
        final VisualizationData data = createVisualizationData();
        data.setProgress(VisualizationData.Progress.DownloadingInput);
        testObject.add(id, data);

        testObject.getLinks(id, Permission.getPublicPermission());
    }

    @Test
    public void getPlan() {

        final String id = "poiu";
        final int planIndex = 0;
        VisualizationData data = createVisualizationData();
        testObject.add(id, data);

        FeatureCollection collection = testObject.getPlan(id, planIndex, Permission.getPublicPermission());

        assertEquals(data.getSimulationData().getPlan(planIndex), collection);
    }

    @Test
    public void getSnapshots() throws IOException {


        VisualizationData data = createVisualizationData();
        final String id = "poiu";
        final double fromTimestep = data.getSimulationData().getFirstTimestep();
        final int numberOfTimesteps = 1;
        final double speedFactor = 1;
        testObject.add(id, data);

        ByteArrayOutputStream stream = testObject.getSnapshots(id, fromTimestep, numberOfTimesteps, speedFactor, Permission.getPublicPermission());

        assertNotNull(stream); // this is a stupid test
    }

    @Test
    public void getConfiguration() {

        VisualizationData data = createVisualizationData();
        final String id = "poiu";
        testObject.add(id, data);

        ConfigurationResponse response = testObject.getConfiguration(id, Permission.getPublicPermission());

        assertEquals(data.getSimulationData().getFirstTimestep(), response.getFirstTimestep());
        assertEquals(data.getSimulationData().getLastTimestep(), response.getLastTimestep());
        assertEquals(data.getSimulationData().getTimestepSize(), response.getTimestepSize());
        TestCase.assertEquals(data.getProgress(), response.getProgress());
        assertNotNull(response.getBounds());
    }

    @Test
    public void getConfiguration_requestWithNonPublicPermission_configuration() {

        VisualizationData data = createVisualizationData();
        final String id = "poiu";
        testObject.add(id, data);

        ConfigurationResponse response = testObject.getConfiguration(id, Permission.createFromAuthId("any-id"));

        assertEquals(data.getSimulationData().getFirstTimestep(), response.getFirstTimestep());
        assertEquals(data.getSimulationData().getLastTimestep(), response.getLastTimestep());
        assertEquals(data.getSimulationData().getTimestepSize(), response.getTimestepSize());
        TestCase.assertEquals(data.getProgress(), response.getProgress());
        assertNotNull(response.getBounds());
    }

    @Test(expected = ForbiddenException.class)
    public void getConfiguration_noPermission_exception() {

        VisualizationData data = createVisualizationData();
        final String id = "poiu";
        Set<Permission> privatePermissions = new HashSet<>();
        privatePermissions.add(Permission.createFromAuthId("some-id"));
        data.setPermissions(privatePermissions);
        testObject.add(id, data);

        testObject.getConfiguration(id, Permission.createFromAuthId("some-other-id"));

        fail("invalid id should cause exception");
    }

    @Test(expected = InvalidInputException.class)
    public void getConfiguration_notInDataset() {

        testObject.dataController = mock(DataController.class);
        testObject.getConfiguration("invalid-id", Permission.getPublicPermission());
    }

    @Test
    public void getConfiguration_notDoneYet() {

        final String id = "sdaf";
        final VisualizationData data = createVisualizationData();
        data.setProgress(VisualizationData.Progress.DownloadingInput);
        testObject.add(id, data);

        ConfigurationResponse response = testObject.getConfiguration(id, Permission.getPublicPermission());

        TestCase.assertEquals(data.getProgress(), response.getProgress());
        assertEquals(0.0, response.getFirstTimestep());
        assertEquals(0.0, response.getLastTimestep());
        assertEquals(1.0, response.getTimestepSize());
    }

    private VisualizationData createVisualizationData() {
        VisualizationData result = new VisualizationData();
        result.setProgress(VisualizationData.Progress.Done);
        result.setSimulationData(TestUtils.getSimulationData());
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.getPublicPermission());
        result.setPermissions(permissions);
        return result;
    }
}
