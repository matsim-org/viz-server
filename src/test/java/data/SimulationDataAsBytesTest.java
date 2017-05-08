package data;

import contracts.AgentSnapshotContract;
import contracts.SnapshotContract;
import org.junit.Before;
import org.junit.Test;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import utils.TestUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimulationDataAsBytesTest {

    private SimulationDataAsBytes testObject;

    @Before
    public void setUp() {
        testObject = new SimulationDataAsBytes(2);
    }

    @Test
    public void addSnapshot() throws IOException {

        //arrange
        AgentSnapshotInfo info = TestUtils.createAgentSnapshotInfo(1, 1, 1);
        SnapshotContract contract = new SnapshotContract(1);
        contract.add(info);

        //act
        testObject.addSnapshot(contract);

        //assert
        assertEquals(contract.getTime(), testObject.getFirstTimestep(), 0.001);
        assertEquals(contract.getTime(), testObject.getLastTimestep(), 0.0001);
    }

    @Test
    public void getSnapshots() throws IOException {

        //arrange
        List<SnapshotContract> contracts = new ArrayList<>();
        for (int time = 100; time < 300; time += 2) {
            List<AgentSnapshotInfo> infos = TestUtils.createAgentSnapshotInfos(100);
            SnapshotContract contract = new SnapshotContract(time);
            for (AgentSnapshotInfo info : infos) {
                contract.add(info);
            }
            testObject.addSnapshot(contract);
            contracts.add(contract);
        }

        //act
        byte[] result = testObject.getSnapshots(150, 50);

        //assert
        assertNotNull(result);
        ByteBuffer buffer = ByteBuffer.wrap(result);
        buffer.order(ByteOrder.BIG_ENDIAN);

        int contractsIndex = 25;

        while (buffer.position() < buffer.capacity()) {

            float time = buffer.getFloat();
            float size = buffer.getFloat();
            SnapshotContract expectedContract = contracts.get(contractsIndex);
            assertEquals((float) expectedContract.getTime(), time, 0.001);
            assertEquals((float) expectedContract.getAgentContracts().size() * 2, size, 0.001);

            for (int i = 0; i < size / 2; i++) {
                float x = buffer.getFloat();
                float y = buffer.getFloat();
                AgentSnapshotContract position = expectedContract.getAgentContracts().get(i);
                assertEquals((float) position.getX(), x, 0.001);
                assertEquals((float) position.getY(), y, 0.001);
            }
            contractsIndex++;
        }
    }
}
