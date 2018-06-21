package org.matsim.webvis.frameAnimation.requestHandling;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.contracts.RectContract;
import org.matsim.webvis.frameAnimation.contracts.SnapshotRequest;
import org.matsim.webvis.frameAnimation.data.SimulationDataDAO;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SnapshotRequestHandlerTest {

    private static SnapshotRequestHandler testObject;
    private static SimulationDataDAO simulationDataDAO = new SimulationDataDAO();
    private static String vizId = "id";

    @BeforeClass
    public static void setUp() {
        testObject = new SnapshotRequestHandler();
    }


    @BeforeClass
    public static void setUpClass() {

        simulationDataDAO.add(vizId, TestUtils.getDataProvider());
    }

    @AfterClass
    public static void tearDownClass() {
        simulationDataDAO.remove(vizId);
    }

    @Test
    public void processTest() {

        //arrange
        RectContract bounds = new RectContract(-1000, 1000, -1000, 1000);
        SnapshotRequest request = new SnapshotRequest(vizId, 25210, 10, 1);

        //act
        Answer answer = testObject.process(request);

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        assertTrue(answer.hasEncodedMessage());
        assertTrue(answer.getEncodedMessage().length > 0);
    }

    @Test
    public void processTest_BadRequest() {

        //arrange
        RectContract bounds = new RectContract(-1000, 1000, -1000, 1000);
        SnapshotRequest request = new SnapshotRequest(vizId, -1, 10, 1);

        //act
        Answer answer = testObject.process(request);

        //assert
        assertEquals(Params.STATUS_BADREQUEST, answer.getCode());
        assertTrue(answer.hastText());
    }
}
