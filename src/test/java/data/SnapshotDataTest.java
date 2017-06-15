package data;

import contracts.SnapshotContract;
import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import utils.TestUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SnapshotDataTest {

    private SnapshotData testObject;

    @Before
    public void setUp() {
        testObject = new SnapshotData(2);
    }

    @Test
    public void addSnapshot() throws IOException {

        //arrange
        AgentSnapshotInfo info = TestUtils.createAgentSnapshotInfo(1, 1, 1);
        SnapshotContract contract = new SnapshotContract(1);
        contract.add(info, 1);

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
        List<List<AgentSnapshotInfo>> snapshotInfos = new ArrayList<>();

        for (int time = 100; time < 300; time += 2) {
            List<AgentSnapshotInfo> infos = TestUtils.createAgentSnapshotInfos(100);
            snapshotInfos.add(infos);
            SnapshotContract contract = new SnapshotContract(time);
            for (int i = 0; i < infos.size(); i++) {
                AgentSnapshotInfo info = infos.get(i);
                contract.add(info, i);
            }
            testObject.addSnapshot(contract);
            contracts.add(contract);
        }

        //act
        byte[] result = testObject.getSnapshots(150, 50, 1);

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

            List<AgentSnapshotInfo> expectedInfos = snapshotInfos.get(contractsIndex);
            assertEquals((float) expectedInfos.size() * 3, size, 0.001);

            for (int i = 0; i < size / 3; i++) {
                float x = buffer.getFloat();
                float y = buffer.getFloat();
                float id = buffer.getFloat();
                AgentSnapshotInfo info = expectedInfos.get(i);
                assertEquals((float) info.getEasting(), x, 0.001);
                assertEquals((float) info.getNorthing(), y, 0.001);
                assertEquals(i, id, 0.001);
            }
            contractsIndex++;
        }
    }

    @Test
    public void getId() throws IOException {

        //arrange
        List<SnapshotContract> contracts = new ArrayList<>();
        List<List<AgentSnapshotInfo>> snapshotInfos = new ArrayList<>();

        for (int time = 100; time < 300; time += 2) {
            List<AgentSnapshotInfo> infos = TestUtils.createAgentSnapshotInfos(100);
            snapshotInfos.add(infos);
            SnapshotContract contract = new SnapshotContract(time);
            for (int i = 0; i < infos.size(); i++) {
                AgentSnapshotInfo info = infos.get(i);
                int index = testObject.addId(info.getId());
                contract.add(info, index);
            }
            testObject.addSnapshot(contract);
            contracts.add(contract);
        }

        List<AgentSnapshotInfo> infosOfFirstSnapshot = snapshotInfos.get(0);
        for (int i = 0; i < 100; i++) {

            //act
            Id id = testObject.getId(i);

            //assert
            assertEquals(infosOfFirstSnapshot.get(i).getId(), id);
        }
    }
}
