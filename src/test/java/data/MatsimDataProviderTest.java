package data;

import contracts.RectContract;
import contracts.SnapshotContract;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;
import utils.TestUtils;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MatsimDataProviderTest {

    private static MatsimDataProvider testObject;

    @BeforeClass
    public static void setUp() {
        testObject = new MatsimDataProvider(TestUtils.NETWORK_FILE, TestUtils.EVENTS_FILE, 1);
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
    public void getSnapshotTest() {

        //arrange
        QuadTree.Rect bounds = new QuadTree.Rect(-500, -500, 500, 500);
        double startTime = 25202;
        int size = 2;

        //act
        List<SnapshotContract> result = testObject.getSnapshot(bounds, startTime, size);

        //assert
        assertEquals(size, result.size());
        assertEquals(startTime, result.get(0).getTime(), 0.0001);
        assertEquals(startTime + 1, result.get(1).getTime(), 0.0001);
    }

    @Test
    public void getBoundsTest() {

        //arrange
        final double left = -2500;
        final double right = 1001;
        final double top = 401;
        final double bottom = -1000;

        //act
        RectContract bounds = testObject.getBounds();

        //assert
        assertEquals(left, bounds.getLeft(), 0.1);
        assertEquals(right, bounds.getRight(), 0.1);
        assertEquals(top, bounds.getTop(), 0.1);
        assertEquals(bottom, bounds.getBottom(), 0.1);
    }
}
