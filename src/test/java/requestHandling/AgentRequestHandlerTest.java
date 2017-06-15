package requestHandling;

import constants.Params;
import contracts.AgentRequest;
import contracts.RectContract;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AgentRequestHandlerTest {

    private static AgentRequestHandler testObject;

    @BeforeClass
    public static void setUp() {
        testObject = new AgentRequestHandler(TestUtils.getDataProvider());
    }

    @Test
    public void processTest() {

        //arrange
        RectContract bounds = new RectContract(-1000, 1000, -1000, 1000);
        AgentRequest request = new AgentRequest(bounds, 25210, 10, 1);

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
        AgentRequest request = new AgentRequest(bounds, -1, 10, 1);

        //act
        Answer answer = testObject.process(request);

        //assert
        assertEquals(Params.STATUS_BADREQUEST, answer.getCode());
        assertTrue(answer.hastText());
    }
}
