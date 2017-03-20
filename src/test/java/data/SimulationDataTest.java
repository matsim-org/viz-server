package data;

import contracts.SnapshotContract;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class SimulationDataTest {

    private SimulationData testObject;

    @Before
    public void setUp() {
        testObject = new SimulationData();
    }

    @Test
    public void addSnapshot_SnapshotAdded() {

        //arrange
        final double time = 1;
        SnapshotContract snapshot = new SnapshotContract(time);

        //act
        try {
            testObject.addSnapshot(snapshot);
        } catch (Exception e) {
            fail("Exception was thrown with message: " + e.getMessage());
        }

        //assert
        assertNotNull(testObject.getSnapshot(time));
        assertEquals(snapshot, testObject.getSnapshot(time));
    }

    @Test(expected = Exception.class)
    public void addSnapshot_snapShotAlreadyPresent() throws Exception {

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
    public void addSnapshot_firstAndLastTimestepsAreSet() throws Exception {

        //arrange
        final double firstTimestep = 1;
        final double middleTimestep = 2;
        final double lastTimestep = 3;
        SnapshotContract firstSnapshot = new SnapshotContract(firstTimestep);
        SnapshotContract middleSnapshot = new SnapshotContract(middleTimestep);
        SnapshotContract lastSnapshot = new SnapshotContract(lastTimestep);

        //act
        testObject.addSnapshot(lastSnapshot);
        testObject.addSnapshot(middleSnapshot);
        testObject.addSnapshot(firstSnapshot);

        //assert
        assertEquals(firstTimestep, testObject.getFirstTimestep());
        assertEquals(lastTimestep, testObject.getLastTimestep());
    }
}
