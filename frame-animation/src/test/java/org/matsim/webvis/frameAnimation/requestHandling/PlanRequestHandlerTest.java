package org.matsim.webvis.frameAnimation.requestHandling;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.contracts.PlanRequest;
import org.matsim.webvis.frameAnimation.data.SimulationDataDAO;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlanRequestHandlerTest {

    private static PlanRequestHandler testObject;
    private static SimulationDataDAO simulationDataDAO = new SimulationDataDAO();
    private static String vizId = "id";

    @BeforeClass
    public static void setUp() {
        testObject = new PlanRequestHandler();
        simulationDataDAO.add(vizId, TestUtils.getDataProvider());
    }

    @AfterClass
    public static void tearDownClass() {
        simulationDataDAO.remove(vizId);
    }

    @Test
    public void processTest() {

        //arrange
        PlanRequest body = new PlanRequest(vizId, 1);
        //there is probably a smarter way to test this...
        String expectedJson = "{\"features\":[{\"type\":\"Feature\",\"properties\":{\"type\":\"leg\"},\"geometry\":" +
                "{\"coordinates\":[[-2000.0,0.0],[-1500.0,0.0],[-1500.0,0.0],[-469.8,400.0],[-469.8,400.0]," +
                "[-439.8,400.0],[-439.8,400.0],[0.0,0.0],[0.0,0.0],[1000.0,0.0]],\"type\":\"LineString\"}}],\"type\":" +
                "\"FeatureCollection\"}";

        //act
        Answer answer = testObject.process(body);

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        assertTrue(answer.hastText());
        assertEquals(expectedJson, answer.getText());
    }

    @Test
    public void processTest_badRequest() {

        //arrange
        PlanRequest body = new PlanRequest(vizId, 1000);

        //act
        Answer answer = testObject.process(body);

        //assert
        assertEquals(Params.STATUS_BADREQUEST, answer.getCode());
        assertTrue(answer.hastText());
    }
}
