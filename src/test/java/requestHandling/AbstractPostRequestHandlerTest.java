package requestHandling;


import com.google.gson.Gson;
import constants.Params;
import contracts.LinkContract;
import contracts.RectContract;
import data.MatsimDataProvider;
import org.junit.Before;
import org.junit.Test;
import utils.TestUtils;

import static org.junit.Assert.assertEquals;

public class AbstractPostRequestHandlerTest {

    private AbstractPostRequestHandlerTestable testObject;

    @Before
    public void setUp() {
        testObject = new AbstractPostRequestHandlerTestable();
    }

    @Test
    public void processBodyTest() {
        //arrange
        String bodyText = "{\"left\":-1000,\"right\":0,\"top\":-1000,\"bottom\":0}";

        //act
        Answer answer = testObject.processBodyImpl(bodyText);

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        LinkContract[] contracts = new Gson().fromJson(answer.getBody(), LinkContract[].class);
        assertEquals(4, contracts.length);
    }

    @Test
    public void processBodyTest_wrongJSONObject_zeroLinks() {
        //arrange
        String bodyText = "{\"wrong\":-1000,\"json\":0,\"object\":-1000, \"body\": 0}";

        //act
        Answer answer = testObject.processBodyImpl(bodyText);

        //assert
        assertEquals(Params.STATUS_OK, answer.getCode());
        LinkContract[] contracts = new Gson().fromJson(answer.getBody(), LinkContract[].class);
        assertEquals(0, contracts.length);
    }

    @Test
    public void processBodyTest_Status400() {

        //arrange
        String bodyText = "poorly formatted request body";

        //act
        Answer answer = testObject.processBodyImpl(bodyText);

        //assert
        assertEquals(Params.STATUS_BADREQUEST, answer.getCode());
    }

    private class AbstractPostRequestHandlerTestable extends NetworkRequestHandler {

        public AbstractPostRequestHandlerTestable() {
            super(new MatsimDataProvider(TestUtils.NETWORK_FILE, TestUtils.EVENTS_FILE, 1));
        }

        public Answer processBodyImpl(String body) {
            return processBody(body);
        }

        @Override
        public Answer process(RectContract body) {
            return super.process(body);
        }
    }
}
