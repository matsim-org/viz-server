package org.matsim.webvis.frameAnimation.data;

import org.junit.Test;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import static org.junit.Assert.assertNotNull;

public class MatsimDataReaderTest {

    @Test
    public void read() {

        //arrange
        MatsimDataReader reader =
                new MatsimDataReader(TestUtils.NETWORK_FILE, TestUtils.EVENTS_FILE, TestUtils.POPULATION_FILE);

        //act
        reader.readAllFiles(2);

        //assert
        assertNotNull(reader.getNetworkData());
        assertNotNull(reader.getSnapshotData());
        assertNotNull(reader.getPopulationData());
    }
}
