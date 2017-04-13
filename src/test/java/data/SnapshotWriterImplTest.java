package data;

import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfoFactory;
import org.matsim.vis.snapshotwriters.SnapshotLinkWidthCalculator;
import org.matsim.webvis.contracts.Contracts;

import static org.junit.Assert.*;

public class SnapshotWriterImplTest {

    private SnapshotWriterImpl testObject;

    @Before
    public void setUp() {
        testObject = new SnapshotWriterImpl(1);
    }

    @Test
    public void beginSnapshotTest() {

        //arrange
        final double time = 0;
        //act
        testObject.beginSnapshot(time);

        //assert
        testObject.endSnapshot();
        Contracts.SimulationData result = testObject.getSimulationData();
        assertNotNull(result);
        assertEquals(1, result.getSnapshotsCount());
        assertEquals(0, result.getFirstTimestep(), 0.1);
        assertEquals(0, result.getLastTimestep(), 0.1);
        Contracts.Snapshot snapshot = result.getSnapshots(0);
        assertEquals(time, snapshot.getTime(), 0.1);
    }

    @Test
    public void endSnaphotTest() {

        //arrange
        final double timestep = 1;
        testObject.beginSnapshot(timestep);

        //act
        testObject.endSnapshot();

        //assert
        Contracts.SimulationData result = testObject.getSimulationData();
        assertNotNull(result);
        assertEquals(1, result.getSnapshotsCount());
        Contracts.Snapshot snapshot = result.getSnapshots(0);
        assertEquals(timestep, snapshot.getTime(), 0.1);
    }

    @Test
    public void addAgentTest() {

        //arrange
        final String id = "id";
        final double northing = 12;
        final double easting = 13;
        final double timestep = 2;
        testObject.beginSnapshot(timestep);
        AgentSnapshotInfoFactory factory = new AgentSnapshotInfoFactory(new SnapshotLinkWidthCalculator());
        AgentSnapshotInfo info = factory.createAgentSnapshotInfo(Id.createPersonId(id), easting, northing, 1, 1);

        //act
        testObject.addAgent(info);

        //assert
        testObject.endSnapshot();
        Contracts.SimulationData result = testObject.getSimulationData();
        assertNotNull(result);

        Contracts.Snapshot snapshot = result.getSnapshots(0);
        assertEquals(timestep, snapshot.getTime(), 0.1);
        assertEquals(1, snapshot.getPositionsCount());

        Contracts.Position position = snapshot.getPositions(0);
        assertEquals(id, position.getAgentId());
        assertEquals(easting, position.getX(), 0.1);
        assertEquals(northing, position.getY(), 0.1);
    }

    @Test
    public void finishTest() {
        //nothing to test, since nothing is done
        assertTrue(true);
    }
}
