package data;

import contracts.AgentSnapshotContract;
import contracts.SnapshotContract;
import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfoFactory;
import org.matsim.vis.snapshotwriters.SnapshotLinkWidthCalculator;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SnapshotWriterImplTest {

    private SnapshotWriterImpl testObject;

    @Before
    public void setUp() {
        testObject = new SnapshotWriterImpl();
    }

    @Test
    public void beginSnapshotTest() {

        //act
        testObject.beginSnapshot(0);

        //assert
        List<SnapshotContract> result = testObject.getSnapshots();
        assertEquals(0, result.size());
    }

    @Test
    public void endSnaphotTest() {

        //arrange
        testObject.beginSnapshot(1);

        //act
        testObject.endSnapshot();

        //assert
        List<SnapshotContract> result = testObject.getSnapshots();
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getTime(), 0.1);
    }

    @Test
    public void addAgentTest() {

        //arrange
        final String id = "id";
        final double northing = 12;
        final double easting = 13;
        testObject.beginSnapshot(2);
        AgentSnapshotInfoFactory factory = new AgentSnapshotInfoFactory(new SnapshotLinkWidthCalculator());
        AgentSnapshotInfo info = factory.createAgentSnapshotInfo(Id.createPersonId(id), easting, northing, 1, 1);

        //act
        testObject.addAgent(info);

        //assert
        testObject.endSnapshot();
        List<SnapshotContract> result = testObject.getSnapshots();
        assertEquals(1, result.size());
        SnapshotContract contract = result.get(0);
        assertEquals(2, contract.getTime(), 0.1);
        assertEquals(1, contract.getAgentInformations().size());
        AgentSnapshotContract agent = contract.getAgentInformations().get(0);
        assertEquals(id, agent.getId());
        assertEquals(northing, agent.getX(), 0.1);
        assertEquals(easting, agent.getY(), 0.1);
    }

    @Test
    public void finishTest() {

    }
}
