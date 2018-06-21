package org.matsim.webvis.frameAnimation.requestHandling;

import com.google.gson.Gson;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.webvis.frameAnimation.contracts.RectContract;
import org.matsim.webvis.frameAnimation.contracts.VisualizationRequest;
import org.matsim.webvis.frameAnimation.data.SimulationDataDAO;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigurationRequestHandlerTest {

    private static ConfigurationRequestHandler testObject;
    private static String vizId = "id";
    private static SimulationDataDAO simulationDataDAO = new SimulationDataDAO();

    @BeforeClass
    public static void setUp() {

        testObject = new ConfigurationRequestHandler();
        simulationDataDAO.add(vizId, TestUtils.getDataProvider());
    }

    @AfterClass
    public static void tearDownClass() {
        simulationDataDAO.remove(vizId);
    }

    @Test
    public void processTest_Ok() {

        //arrange
        final double left = -2500;
        final double right = 1000;
        final double top = 400;
        final double bottom = -1000;

        //act
        Answer answer = testObject.process(new VisualizationRequest(vizId));

        //assert
        assertNotNull(answer);
        assertEquals(Params.STATUS_OK, answer.getCode());
        RectContract bounds = new RectContract(left, right, top, bottom);
        ConfigurationResponse response = new Gson().fromJson(answer.getText(), ConfigurationResponse.class);
        assertEquals(bounds.getLeft(), response.getBounds().getLeft(), 0.001);
        assertEquals(bounds.getRight(), response.getBounds().getRight(), 0.001);
        assertEquals(bounds.getTop(), response.getBounds().getTop(), 0.001);
        assertEquals(bounds.getBottom(), response.getBounds().getBottom(), 0.001);
    }

}
