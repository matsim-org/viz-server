package org.matsim.webvis.frameAnimation.data;

import org.junit.Before;
import org.junit.Test;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.webvis.frameAnimation.contracts.SnapshotContract;
import org.matsim.webvis.frameAnimation.utils.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SnapshotWriterImplTest {

    private SnapshotWriterImpl testObject;

    @Before
    public void setUp() {
        testObject = new SnapshotWriterImpl(1);
    }

    @Test
    public void beginSnapshotTest() {

        //arrange
        final double time = 16458;

        //act
        testObject.beginSnapshot(time);

        //assert
        SnapshotContract current = testObject.getCurrentSnapshot();
        assertEquals(time, current.getTime(), 0.001);
    }

    @Test
    public void endSnaphotTest() {

        //arrange
        final double timestep = 1;
        testObject.beginSnapshot(timestep);

        //act
        testObject.endSnapshot();

        //assert
        SnapshotData data = testObject.getSimulationData();
        assertEquals(timestep, data.getLastTimestep(), 0.001);
        assertEquals(timestep, data.getFirstTimestep(), 0.001);
    }

    @Test
    public void addAgentTest() {

        //arrange
        AgentSnapshotInfo info = TestUtils.createAgentSnapshotInfo(1, 12, 13);

        final double timestep = 2;
        testObject.beginSnapshot(timestep);

        //act
        testObject.addAgent(info);

        //assert
        SnapshotContract current = testObject.getCurrentSnapshot();
        assertEquals(timestep, current.getTime(), 0.001);
        assertEquals(info.getEasting(), current.getAgentContracts().get(0).getX(), 0.001);
        assertEquals(info.getNorthing(), current.getAgentContracts().get(0).getY(), 0.001);
    }

    @Test
    public void addAgentTest_StateIsAtActivity_NotAdded() {

        //Arrange
        AgentSnapshotInfo info = TestUtils.createAgentSnapshotInfo(1, 12, 13);
        info.setAgentState(AgentSnapshotInfo.AgentState.PERSON_AT_ACTIVITY);
        final double timestep = 3;
        testObject.beginSnapshot(timestep);

        //act
        testObject.addAgent(info);

        //assert
        SnapshotContract current = testObject.getCurrentSnapshot();
        assertEquals(timestep, current.getTime(), 0.001);
        assertEquals(0, current.getAgentContracts().size());
    }

    @Test
    public void finishTest() {
        //nothing to test, since nothing is done
        assertTrue(true);
    }
}
