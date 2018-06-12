package org.matsim.webvis.frameAnimation.data;

import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

public class DataControllerTest {

    @BeforeClass
    public static void setUp() {
        TestUtils.loadConfig();
        ServiceCommunication.initialize(true);
    }

    @Test
    public void test() {

        DataController controller = DataController.Instance;

        controller.scheduleHourlyFetching();
    }
}
