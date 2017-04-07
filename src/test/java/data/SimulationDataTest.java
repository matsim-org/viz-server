package data;

import contracts.SnapshotContract;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.*;

public class SimulationDataTest {

    private SimulationData testObject;

    @Before
    public void setUp() {
        testObject = new SimulationData(1);
    }

    @Test
    public void addSnapshot_SnapshotAdded() {

        //arrange
        final double time = 1;
        SnapshotContract snapshot = new SnapshotContract(time);
        testObject.addSnapshot(snapshot);

        //assert
        assertNotNull(testObject.getSnapshot(time));
        assertEquals(snapshot, testObject.getSnapshot(time));
    }

    @Test(expected = RuntimeException.class)
    public void addSnapshot_snapShotAlreadyPresent() {

        //arrange
        final double time = 2;
        SnapshotContract snapshot = new SnapshotContract(time);
        SnapshotContract otherSnapshot = new SnapshotContract(time);

        //act
        testObject.addSnapshot(snapshot);
        testObject.addSnapshot(otherSnapshot);

        //assert
        fail("No exception was thrown!");
    }

    @Test
    public void addSnapshot_firstAndLastTimestepsAreSet() {

        //arrange
        final double firstTimestep = 1;
        final double middleTimestep = 2;
        final double lastTimestep = 3;
        SnapshotContract firstSnapshot = new SnapshotContract(firstTimestep);
        SnapshotContract middleSnapshot = new SnapshotContract(middleTimestep);
        SnapshotContract lastSnapshot = new SnapshotContract(lastTimestep);

        //act
        testObject.addSnapshot(firstSnapshot);
        testObject.addSnapshot(middleSnapshot);
        testObject.addSnapshot(lastSnapshot);

        //assert
        assertEquals(firstTimestep, testObject.getFirstTimestep());
        assertEquals(lastTimestep, testObject.getLastTimestep());
    }

    @Test
    public void getSnapshot_correctSnapshotIsReturned() {

        //arrange
        final double firstTimestep = 1;
        final double middleTimestep = 2;
        final double lastTimestep = 3;
        SnapshotContract firstSnapshot = new SnapshotContract(firstTimestep);
        SnapshotContract middleSnapshot = new SnapshotContract(middleTimestep);
        SnapshotContract lastSnapshot = new SnapshotContract(lastTimestep);
        testObject.addSnapshot(firstSnapshot);
        testObject.addSnapshot(middleSnapshot);
        testObject.addSnapshot(lastSnapshot);

        //act
        SnapshotContract middleResult = testObject.getSnapshot(middleTimestep);
        SnapshotContract firstResult = testObject.getSnapshot(firstTimestep);
        SnapshotContract lastResult = testObject.getSnapshot(lastTimestep);

        //assert
        assertEquals(firstSnapshot, firstResult);
        assertEquals(middleSnapshot, middleResult);
        assertEquals(lastSnapshot, lastResult);
    }

    @Test
    public void getSnapshot_timestepSizeOdd_correctSnapshotIsReturned() {

        //arrange
        testObject = new SimulationData(0.5);
        final double firstTimestep = 1;
        final double middleTimestep = 1.5;
        final double lastTimestep = 2.0;
        SnapshotContract firstSnapshot = new SnapshotContract(firstTimestep);
        SnapshotContract middleSnapshot = new SnapshotContract(middleTimestep);
        SnapshotContract lastSnapshot = new SnapshotContract(lastTimestep);
        testObject.addSnapshot(firstSnapshot);
        testObject.addSnapshot(middleSnapshot);
        testObject.addSnapshot(lastSnapshot);

        //act
        SnapshotContract middleResult = testObject.getSnapshot(middleTimestep);
        SnapshotContract firstResult = testObject.getSnapshot(firstTimestep);
        SnapshotContract lastResult = testObject.getSnapshot(lastTimestep);

        //assert
        assertEquals(firstSnapshot, firstResult);
        assertEquals(middleSnapshot, middleResult);
        assertEquals(lastSnapshot, lastResult);
    }

    @Test
    public void getSnapshots_correctSnapshotsReturned() {
        //arrange
        final double timestepSize = 0.5;
        final double firstTimestep = 20;
        final double lastTimestep = 50;
        final double fromTimestep = 22.5;
        final int size = 31;
        testObject = new SimulationData(timestepSize);
        for (double i = firstTimestep; i <= lastTimestep; i += timestepSize) {
            testObject.addSnapshot(new SnapshotContract(i));
        }

        //act
        List<SnapshotContract> result = testObject.getSnapshots(fromTimestep, size);

        //assert
        assertEquals(size, result.size());

        SnapshotContract first = result.get(0);
        SnapshotContract last = result.get(result.size() - 1);
        assertEquals(fromTimestep, first.getTime(), 0.0001);
        double expectedLastTime = fromTimestep + size * timestepSize - timestepSize;
        assertEquals(expectedLastTime, last.getTime(), 0.0001);
    }

    @Test(expected = RuntimeException.class)
    public void getSnapshots_fromTimestepTooSmall() {

        //arrange
        final double timestepSize = 0.5;
        final double firstTimestep = 20;
        final double lastTimestep = 50;
        final double fromTimestep = 22.5;
        final int size = 31;
        testObject = new SimulationData(timestepSize);
        for (double i = firstTimestep; i <= lastTimestep; i += timestepSize) {
            testObject.addSnapshot(new SnapshotContract(i));
        }

        //act
        List<SnapshotContract> result = testObject.getSnapshots(firstTimestep - 1, 1);
    }

    @Test
    public void getSnapshots_moreFramesThanAvailable() {

        //arrange
        final double timestepSize = 0.5;
        final double firstTimestep = 20;
        final double lastTimestep = 50;
        final double fromTimestep = 22.5;
        final int size = 31;
        testObject = new SimulationData(timestepSize);
        for (double i = firstTimestep; i <= lastTimestep; i += timestepSize) {
            testObject.addSnapshot(new SnapshotContract(i));
        }

        //act
        List<SnapshotContract> result = testObject.getSnapshots(lastTimestep - 1, 10);

        //assert
        assertEquals(3, result.size());

    }
}
