package org.matsim.webvis.frameAnimation.data;

import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.webvis.frameAnimation.contracts.RectContract;
import org.matsim.webvis.frameAnimation.contracts.geoJSON.FeatureCollection;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

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
    public void getLinksTest() {

        //arrange
        QuadTree.Rect bounds = new QuadTree.Rect(-500, -500, 500, 500);

        //act
       /* byte[] result = testObject.getLinks(bounds);

        //assert
        assertNotNull(result);
        assertEquals(80, result.length); //this is very deterministic...
        */
    }

    @Test
    public void getSnapshotTest() throws IOException {

        //arrange
        QuadTree.Rect bounds = new QuadTree.Rect(-500, -500, 500, 500);
        double startTime = testObject.getFirstTimestep();
        int size = 2;

        //act
        byte[] result = testObject.getSnapshots(bounds, startTime, size, 1);

        //assert
        assertNotNull(result);
        assertEquals(64, result.length);
    }

    @Test
    public void getPlanTest() {

        //arrange
        String expectedJson = "{\"features\":[{\"type\":\"Feature\",\"properties\":{\"type\":\"leg\"},\"geometry\":" +
                "{\"coordinates\":[[-2000.0,0.0],[-1500.0,0.0],[-1500.0,0.0],[-469.8,-400.0],[-469.8,-400.0]," +
                "[-439.8,-400.0],[-439.8,-400.0],[0.0,0.0],[0.0,0.0],[1000.0,0.0]],\"type\":\"LineString\"}}],\"type\":" +
                "\"FeatureCollection\"}";

        //act
        FeatureCollection result = testObject.getPlan(33);

        //assert
        assertEquals(expectedJson, result.toGeoJson());
    }

    @Test
    public void getBoundsTest() {

        //arrange
        final double left = -2500;
        final double right = 1000.0;
        final double top = 400;
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
