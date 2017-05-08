package requestHandling;


import com.google.gson.Gson;
import constants.Params;
import contracts.RectContract;
import data.MatsimDataProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.TestUtils;

import static org.junit.Assert.*;

public class AbstractPostRequestHandlerTest {

    private static AbstractPostRequestHandlerTestable testObject;

    @BeforeClass
    public static void setUp() {

        testObject = new AbstractPostRequestHandlerTestable();
    }

    @Test
    public void processBodyTest() {
        //arrange
        String bodyText = "{\"left\":-1000.0,\"right\":0.0,\"top\":-1000.0,\"bottom\":0.0}";

        //act
        Answer answer = testObject.processBodyImpl(bodyText);

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        assertEquals(bodyText, answer.getText());
    }

    @Test
    public void processBodyTest_wrongJSONObject() {
        //arrange
        String bodyText = "{\"wrong\":-1000.0,\"parameters\":0.0,\"in\":-1000.0,\"request\":0.0}";
        String expected = "{\"left\":0.0,\"right\":0.0,\"top\":0.0,\"bottom\":0.0}";

        //act
        Answer answer = testObject.processBodyImpl(bodyText);

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        assertTrue(answer.hastText());
        assertEquals(expected, answer.getText());
    }

    @Test
    public void processBodyTest_Status400() {

        //arrange
        String bodyText = "poorly formatted request body";

        //act
        Answer answer = testObject.processBodyImpl(bodyText);

        //assert
        assertEquals(Params.STATUS_BADREQUEST, answer.getCode());
        assertTrue(answer.hastText());
        assertNotNull(answer.getText());
    }

    private static class AbstractPostRequestHandlerTestable extends AbstractPostRequestHandler<RectContract> {

        public AbstractPostRequestHandlerTestable() {
            super(RectContract.class, new MatsimDataProvider(TestUtils.NETWORK_FILE, TestUtils.EVENTS_FILE, 1));
        }

        public Answer processBodyImpl(String body) {
            return processBody(body);
        }

        @Override
        public Answer process(RectContract body) {
            return Answer.ok(new Gson().toJson(body));
        }
    }
}
