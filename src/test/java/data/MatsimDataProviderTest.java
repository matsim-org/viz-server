package data;

import contracts.RectContract;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.core.utils.collections.QuadTree;
import utils.TestUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MatsimDataProviderTest {

    private static MatsimDataProvider testObject;

    @BeforeClass
    public static void setUp() {
        testObject = TestUtils.getDataProvider();
    }

    @Test
    public void getLinksTest() throws IOException {

        //arrange
        QuadTree.Rect bounds = new QuadTree.Rect(-500, -500, 500, 500);

        //act
        byte[] result = testObject.getLinks(bounds);

        //assert
        assertNotNull(result);
        assertEquals(80, result.length); //this is very deterministic...
    }

    @Test
    public void getSnapshotTest() throws IOException {

        //arrange
        QuadTree.Rect bounds = new QuadTree.Rect(-500, -500, 500, 500);
        double startTime = testObject.getFirstTimestep();
        int size = 2;

        //act
        byte[] result = testObject.getSnapshots(bounds, startTime, size);

        //assert
        assertNotNull(result);
        assertEquals(48, result.length);
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
