package org.matsim.webvis.frameAnimation.requestHandling;

import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.contracts.PlanRequest;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlanRequestHandlerTest {

    private static PlanRequestHandler testObject;

    @BeforeClass
    public static void setUp() {
        testObject = new PlanRequestHandler(TestUtils.getDataProvider());
    }

    @Test
    public void processTest() {

        //arrange
        PlanRequest body = new PlanRequest(1);
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
        PlanRequest body = new PlanRequest(1000);

        //act
        Answer answer = testObject.process(body);

        //assert
        assertEquals(Params.STATUS_BADREQUEST, answer.getCode());
        assertTrue(answer.hastText());
    }
}
