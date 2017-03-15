package data;

import contracts.RectContract;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;
import utils.TestUtils;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class MatsimDataProviderTest {

    private static MatsimDataProvider testObject;

    @BeforeClass
    public static void setUp() {
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

    @Test
    public void getBoundsTest() {

        //arrange
        final double left = -2500;
        final double right = 1001;
        final double top = -1000;
        final double bottom = 401;

        //act
        RectContract bounds = testObject.getBounds();

        //assert
        assertEquals(left, bounds.getLeft(), 0.1);
        assertEquals(right, bounds.getRight(), 0.1);
        assertEquals(top, bounds.getTop(), 0.1);
        assertEquals(bottom, bounds.getBottom(), 0.1);
    }
}
