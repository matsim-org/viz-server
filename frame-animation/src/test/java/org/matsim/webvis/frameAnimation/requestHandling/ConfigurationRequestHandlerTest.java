package org.matsim.webvis.frameAnimation.requestHandling;

import com.google.gson.Gson;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.webvis.frameAnimation.contracts.RectContract;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ConfigurationRequestHandlerTest {

    private static ConfigurationRequestHandler testObject;

    @BeforeClass
    public static void setUp() {
        testObject = new ConfigurationRequestHandler(TestUtils.getDataProvider());
    }

    @Test
    public void processTest_Ok() {

        //arrange
        final String id = "id";
        final double left = -2500;
        final double right = 1000;
        final double top = 400;
        final double bottom = -1000;

        //act
        Answer answer = testObject.process(new Object());

        //assert
        assertNotNull(answer);
        assertEquals(Params.STATUS_OK, answer.getCode());
        RectContract bounds = new RectContract(left, right, top, bottom);
        ConfigurationResponse response = new Gson().fromJson(answer.getText(), ConfigurationResponse.class);
        assertEquals(bounds.getLeft(), response.getBounds().getLeft());
        assertEquals(bounds.getRight(), response.getBounds().getRight());
        assertEquals(bounds.getTop(), response.getBounds().getTop());
        assertEquals(bounds.getBottom(), response.getBounds().getBottom());
    }

}
