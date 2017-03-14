package data;

import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;
import utils.TestUtils;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class MatsimDataProviderTest {

    private MatsimDataProvider testObject;

    @BeforeClass
    public void setUp() {
        testObject = new MatsimDataProvider(TestUtils.NETWORK_FILE, TestUtils.EVENTS_FILE);
    }

    @Test
    public void getLinksTest() {

        //arrange
        QuadTree.Rect bounds = new QuadTree.Rect(-500, -500, 500, 500);

        //act
        Collection<Link> result = testObject.getLinks(bounds);

        //assert
        assertEquals(5, result.size());
    }
}
