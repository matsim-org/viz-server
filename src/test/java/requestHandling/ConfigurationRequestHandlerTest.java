package requestHandling;

import constants.Params;
import contracts.ConfigurationRequest;
import data.MatsimDataProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.TestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ConfigurationRequestHandlerTest {

    private static ConfigurationRequestHandler testObject;

    @BeforeClass
    public static void setUp() {
        MatsimDataProvider data = new MatsimDataProvider(TestUtils.NETWORK_FILE, TestUtils.EVENTS_FILE, 1);
        testObject = new ConfigurationRequestHandler(data);
    }

    @Test
    public void processTest_Ok() {

        //arrange
        final String id = "id";
        final double left = -2500;
        final double right = 1001;
        final double top = 401;
        final double bottom = -1000;
        ConfigurationRequest request = new ConfigurationRequest(id);

        //act
        Answer answer = testObject.process(request);

        //assert
        assertNotNull(answer);
        assertEquals(Params.STATUS_OK, answer.getCode());
        /*RectContract bounds = new RectContract(left, right, top, bottom);
        ConfigurationResponse response = new Gson().fromJson(answer.getBody(), ConfigurationResponse.class);
        assertEquals(id, response.getId());
        assertEquals(bounds.getLeft(), response.getBounds().getLeft());
        assertEquals(bounds.getRight(), response.getBounds().getRight());
        assertEquals(bounds.getTop(), response.getBounds().getTop());
        assertEquals(bounds.getBottom(), response.getBounds().getBottom());
        */
        Assert.fail();
    }

}
