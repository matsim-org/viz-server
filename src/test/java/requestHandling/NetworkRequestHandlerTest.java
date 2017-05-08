package requestHandling;

import constants.Params;
import contracts.RectContract;
import data.MatsimDataProvider;
import org.junit.Before;
import org.junit.Test;
import utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NetworkRequestHandlerTest {

    private NetworkRequestHandler testObject;

    @Before
    public void SetUp() {
        MatsimDataProvider data = new MatsimDataProvider(TestUtils.NETWORK_FILE, TestUtils.EVENTS_FILE, 1);
        testObject = new NetworkRequestHandler(data);
    }

    @Test
    public void processTest() {

        //arrange
        RectContract request = new RectContract(-1000, 1000, -1000, 1000);

        //act
        Answer answer = testObject.process(request);

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        assertTrue(answer.hasEncodedMessage());
        assertTrue(answer.getEncodedMessage().length > 0);
    }
}
