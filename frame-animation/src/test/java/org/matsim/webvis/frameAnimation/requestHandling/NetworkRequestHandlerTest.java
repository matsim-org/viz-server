package org.matsim.webvis.frameAnimation.requestHandling;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.contracts.RectContract;
import org.matsim.webvis.frameAnimation.contracts.VisualizationRequest;
import org.matsim.webvis.frameAnimation.data.SimulationDataDAO;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NetworkRequestHandlerTest {

    private NetworkRequestHandler testObject;
    private static String vizId = "id";
    private static SimulationDataDAO simulationDataDAO = new SimulationDataDAO();

    @Before
    public void SetUp() {
        testObject = new NetworkRequestHandler();
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
        RectContract request = new RectContract(-1000, 1000, -1000, 1000);

        //act
        Answer answer = testObject.process(new VisualizationRequest(vizId));

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        assertTrue(answer.hasEncodedMessage());
        assertTrue(answer.getEncodedMessage().length > 0);
    }
}
