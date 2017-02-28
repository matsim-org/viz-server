package requestHandling;

import com.google.gson.Gson;
import constants.Params;
import contracts.LinkContract;
import data.MatsimDataProvider;
import org.junit.Before;
import org.junit.Test;
import utils.TestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class NetworkRequestHandlerTest {

    private NetworkRequestHandler testObject;

    @Before
    public void SetUp() {
        MatsimDataProvider data = new MatsimDataProvider(TestUtils.NETWORK_FILE, TestUtils.EVENTS_FILE);
        testObject = new NetworkRequestHandler(data);
    }

    @Test
    public void processImplTest() {

        //arrange
        Map<String, String[]> parameters = new HashMap<>();
        parameters.put(Params.BOUNDINGBOX_BOTTOM, new String[]{"0"});
        parameters.put(Params.BOUNDINGBOX_TOP, new String[]{"-1000"});
        parameters.put(Params.BOUNDINGBOX_LEFT, new String[]{"-1000"});
        parameters.put(Params.BOUNDINGBOX_RIGHT, new String[]{"0"});
        final int expectedStatus = Params.STATUS_OK;
        final int expectedNumberOfLinks = 4;

        //act
        Answer answer = testObject.process(parameters);

        //assert
        assertEquals(expectedStatus, answer.getCode());

        Gson gson = new Gson();
        LinkContract[] contracts = gson.fromJson(answer.getBody(), LinkContract[].class);
        assertEquals(expectedNumberOfLinks, contracts.length);
    }
}
