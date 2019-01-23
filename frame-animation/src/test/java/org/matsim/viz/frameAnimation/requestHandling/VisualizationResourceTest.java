package org.matsim.viz.frameAnimation.requestHandling;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.error.ForbiddenException;
import org.matsim.viz.frameAnimation.persistenceModel.*;
import org.matsim.viz.frameAnimation.utils.DatabaseTest;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VisualizationResourceTest extends DatabaseTest {

    private static final String visualizationServerId = "viz-id";

    private Visualization visualization;
    private Agent requester;
    private VisualizationResource resource;

    @Before
    public void setUp() {

        requester = new Agent("auth-id");
        visualization = database.inTransaction(() -> {
            val toPersist = new Visualization();
            toPersist.setFilesServerId(visualizationServerId);
            toPersist.setProgress(Visualization.Progress.Done);
            toPersist.setTimestepSize(12);
            val permission = new Permission(requester, toPersist);
            toPersist.getPermissions().add(permission);
            database.getSessionFactory().getCurrentSession().save(toPersist);
            return toPersist;
        });

        resource = new VisualizationResource(database.getSessionFactory());
    }

    @Test
    public void configuration_notDone() {

        database.inTransaction(() -> {
            visualization.setProgress(Visualization.Progress.DownloadingInput);
            database.getSessionFactory().getCurrentSession().save(visualization);
        });

        val response = database.inTransaction(() -> resource.configuration(requester, visualization.getFilesServerId()));

        assertEquals(1, response.getTimestepSize(), .0001);
        assertEquals(visualization.getProgress(), response.getProgress());
    }

    @Test
    public void configuration_progressDone() {

        val response = database.inTransaction(() -> resource.configuration(requester, visualization.getFilesServerId()));

        assertEquals(visualization.getTimestepSize(), response.getTimestepSize(), .0001);
        assertEquals(visualization.getProgress(), response.getProgress());
    }

    @Test
    public void configuration_progressDonePublicPermission() {

        val response = database.inTransaction(() -> resource.configuration(requester, visualization.getFilesServerId()));

        assertEquals(visualization.getTimestepSize(), response.getTimestepSize(), .0001);
        assertEquals(visualization.getProgress(), response.getProgress());
    }

    @Test(expected = ForbiddenException.class)
    public void configuration_progressDoneNoPermission() {

        Agent otherAgent = database.inTransaction(() -> {
            val toPersist = new Agent("other-id");
            database.getSessionFactory().getCurrentSession().save(toPersist);
            return toPersist;
        });

        database.inTransaction(() -> resource.configuration(otherAgent, visualization.getFilesServerId()));

        fail("invalid agent should cause forbidden exception");
    }

    @Test
    public void network() {

        visualization = database.inTransaction(() -> {

            val testNetwork = TestUtils.loadTestNetwork();
            val matsimNetwork = new MatsimNetwork(testNetwork);
            visualization.addNetwork(matsimNetwork);
            database.getSessionFactory().getCurrentSession().save(visualization);
            return visualization;
        });

        val response = database.inTransaction(() -> resource.network(requester, visualization.getFilesServerId()));

        assertNotNull(response);
        assertEquals(visualization.getMatsimNetwork().getData().length, response.length);
        // any idea for a better test?
    }

    @Test
    public void snapshots() {

        visualization = database.inTransaction(() -> {

            Snapshot snapshot = new Snapshot();
            snapshot.setTimestep(1);
            snapshot.setData(new byte[1000]);
            visualization.addSnapshot(snapshot);
            Snapshot other = new Snapshot();
            other.setTimestep(2);
            other.setData(new byte[1000]);
            visualization.addSnapshot(other);
            database.getSessionFactory().getCurrentSession().save(visualization);
            return visualization;
        });

        val response = database.inTransaction(() -> resource.snapshots(requester, visualization.getFilesServerId(), 1, 30, 1));

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // don't know how to extract the content of a response to test whether the correct
        // snapshot is returned...
    }

    @Test
    public void plan() {

        final int planIndex = 1;
        final String expectedResult = "some-geo-json";

        visualization = database.inTransaction(() -> {

            Plan plan = new Plan();
            plan.setIdIndex(planIndex);
            plan.setGeoJson(expectedResult);
            visualization.addPlan(plan);
            database.getSessionFactory().getCurrentSession().save(visualization);
            return visualization;
        });

        val response = database.inTransaction(() -> resource.plan(requester, visualization.getFilesServerId(), planIndex));

        assertEquals(expectedResult, response);
    }
}
