package data;

import org.junit.Test;
import utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MatsimDataReaderTest {

    @Test
    public void readNetworkTest() {

        //act
        NetworkData data = MatsimDataReader.readNetworkFile(TestUtils.NETWORK_FILE);

        //assert
        assertNotNull(data);
    }

    @Test
    public void readEventsTest() {

        //arrange
        final double firstTimestep = 25200.0161;
        final double lastTimestep = 25475.98;
        final double snapshotPeriod = 0.0167;

        //act
        SimulationDataAsBytes data = MatsimDataReader.readEventsFile(TestUtils.EVENTS_FILE, TestUtils.NETWORK_FILE, snapshotPeriod);

        //assert
        assertNotNull(data);

        assertEquals(firstTimestep, data.getFirstTimestep(), 0.1);
        assertEquals(lastTimestep, data.getLastTimestep(), 0.1);
    }
}
