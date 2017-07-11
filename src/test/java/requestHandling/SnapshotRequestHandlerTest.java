package requestHandling;

import constants.Params;
import contracts.SnapshotRequest;
import contracts.RectContract;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SnapshotRequestHandlerTest {

    private static SnapshotRequestHandler testObject;

    @BeforeClass
    public static void setUp() {
        testObject = new SnapshotRequestHandler(TestUtils.getDataProvider());
    }

    @Test
    public void processTest() {

        //arrange
        RectContract bounds = new RectContract(-1000, 1000, -1000, 1000);
        SnapshotRequest request = new SnapshotRequest(bounds, 25210, 10, 1);

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
        SnapshotRequest request = new SnapshotRequest(bounds, -1, 10, 1);

        //act
        Answer answer = testObject.process(request);

        //assert
        assertEquals(Params.STATUS_BADREQUEST, answer.getCode());
        assertTrue(answer.hastText());
    }
}
