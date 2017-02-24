package data;

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
}
