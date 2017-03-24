package data;

import contracts.SnapshotContract;
import org.junit.Test;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;
import utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MatsimDataReaderTest {

    @Test
    public void readNetworkTest() {

        //act
        QuadTree<Link> links = MatsimDataReader.readNetworkFile(TestUtils.NETWORK_FILE);

        //assert
        assertNotNull(links);
        assertEquals(12, links.values().size());
    }

    @Test
    public void readEventsTest() {
        //act
        final double firstTimestep = 25200.0161;
        final double lastTimestep = 25475.98;
        final double snapshotPeriod = 0.0167;
        SimulationData snapshots = MatsimDataReader.readEventsFile(TestUtils.EVENTS_FILE, TestUtils.NETWORK_FILE, snapshotPeriod);

        //assert
        assertNotNull(snapshots);
        assertEquals(firstTimestep, snapshots.getFirstTimestep(), 0.1);
        assertEquals(lastTimestep, snapshots.getLastTimestep(), 0.1);

        for (double i = firstTimestep; i <= lastTimestep; i += snapshotPeriod) {
            SnapshotContract snapshot = snapshots.getSnapshot(i);
            assertNotNull(snapshot);
            assertEquals(i, snapshot.getTime(), 0.1);
        }
    }
}
