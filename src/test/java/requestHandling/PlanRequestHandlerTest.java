package requestHandling;

import org.junit.BeforeClass;
import utils.TestUtils;

public class PlanRequestHandlerTest {

    private static PlanRequestHandler testObject;

    @BeforeClass
    public static void setUp() {
        testObject = new PlanRequestHandler(TestUtils.getDataProvider());
    }

   /* @Test
    public void processTest() {

        //arrange
        PlanRequest body = new PlanRequest(25230, 3);
        //there is probably a smarter way to test this...
        String expectedJson = "{\"features\":[{\"type\":\"Feature\",\"properties\":{\"type\":\"leg\"},\"geometry\":" +
                "{\"coordinates\":[[-2000.0,0.0],[-1500.0,0.0],[-1500.0,0.0],[-469.8,-400.0],[-469.8,-400.0]," +
                "[-439.8,-400.0],[-439.8,-400.0],[0.0,0.0],[0.0,0.0],[1000.0,0.0]],\"type\":\"LineString\"}}],\"type\":" +
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
        PlanRequest body = new PlanRequest(TestUtils.getDataProvider().getLastTimestep(), 100);

        //act
        Answer answer = testObject.process(body);

        //assert
        assertEquals(Params.STATUS_BADREQUEST, answer.getCode());
        assertTrue(answer.hastText());
    }
    */
}
