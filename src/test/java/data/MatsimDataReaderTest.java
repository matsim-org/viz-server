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
        final double firstTimestep = 25201;
        final double lastTimestep = 25476;
        SimulationData snapshots = MatsimDataReader.readEventsFile(TestUtils.EVENTS_FILE, TestUtils.NETWORK_FILE, 1);

        //assert
        assertNotNull(snapshots);
        assertEquals(firstTimestep, snapshots.getFirstTimestep(), 0.1);
        assertEquals(lastTimestep, snapshots.getLastTimestep(), 0.1);

        for (int i = (int) firstTimestep; i <= lastTimestep; i++) {
            SnapshotContract snapshot = snapshots.getSnapshot(i);
            assertNotNull(snapshot);
            assertEquals(i, (int) snapshot.getTime());
        }
    }
}
