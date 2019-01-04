package org.matsim.viz.frameAnimation.data;

import org.junit.Test;
import org.matsim.viz.frameAnimation.utils.TestUtils;

import static org.junit.Assert.assertNotNull;

public class MatsimDataReaderTest {

    @Test
    public void read() {

        //arrange
        MatsimDataReader reader =
                new MatsimDataReader(TestUtils.NETWORK_FILE_PATH, TestUtils.EVENTS_FILE_PATH, TestUtils.POPULATION_FILE_PATH);

        //act
        reader.readAllFiles(2);

        //assert
        assertNotNull(reader.getNetworkData());
        assertNotNull(reader.getSnapshotData());
        assertNotNull(reader.getPopulationData());
    }
}
