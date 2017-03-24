package data;

import contracts.AgentSnapshotContract;
import contracts.SnapshotContract;
import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfoFactory;
import org.matsim.vis.snapshotwriters.SnapshotLinkWidthCalculator;

import static org.junit.Assert.*;

public class SnapshotWriterImplTest {

    private SnapshotWriterImpl testObject;

    @Before
    public void setUp() {
        testObject = new SnapshotWriterImpl(1);
    }

    @Test
    public void beginSnapshotTest() {

        //act
        testObject.beginSnapshot(0);

        //assert
        SimulationData result = testObject.getSimulationData();
        assertNotNull(result);
    }

    @Test
    public void endSnaphotTest() {

        //arrange
        final double timestep = 1;
        testObject.beginSnapshot(timestep);

        //act
        testObject.endSnapshot();

        //assert
        SimulationData result = testObject.getSimulationData();
        assertNotNull(result);
        assertEquals(1, result.getSnapshots().size());
        assertEquals(timestep, result.getSnapshot(timestep).getTime(), 0.1);
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
        SimulationData result = testObject.getSimulationData();
        assertNotNull(result);

        SnapshotContract contract = result.getSnapshot(timestep);
        assertEquals(2, contract.getTime(), 0.1);
        assertEquals(1, contract.getAgentInformations().size());

        AgentSnapshotContract agent = contract.getAgentInformations().get(0);
        assertEquals(id, agent.getId());
        assertEquals(easting, agent.getX(), 0.1);
        assertEquals(northing, agent.getY(), 0.1);
    }

    @Test
    public void finishTest() {
        //nothing to test, since nothing is done
        assertTrue(true);
    }
}
