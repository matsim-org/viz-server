package contracts;

import org.junit.Before;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfoFactory;
import org.matsim.vis.snapshotwriters.SnapshotLinkWidthCalculator;
import utils.TestUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SnapshotContractTest {

    private SnapshotContract testObject;

    @Before
    public void setUp() {
        testObject = new SnapshotContract(1);
    }

    @Test
    public void add_oneSnapshot() {

        //arrange
        AgentSnapshotInfo info = TestUtils.createAgentSnapshotInfo(1, 1, 1);

        //act
        testObject.add(info);

        //assert
        testObject.encodeSnapshot();
        byte[] snapshots = testObject.getEncodedMessage();
        ByteBuffer buffer = ByteBuffer.wrap(snapshots);
        buffer.order(ByteOrder.BIG_ENDIAN);

        //the snapshots should include time, sizeOfPositions, positionX, positionY
        float time = buffer.getFloat();
        assertEquals(testObject.getTime(), time, 0.001);
        float size = buffer.getFloat();
        assertEquals(2.0, size, 0.001);
        float x = buffer.getFloat();
        assertEquals(info.getEasting(), x, 0.001);
        float y = buffer.getFloat();
        assertEquals(info.getNorthing(), y, 0.001);
        assertEquals(buffer.capacity(), buffer.position());

        Id id = testObject.getIdForIndex(0);
        assertEquals(info.getId(), id);
    }

    @Test
    public void add_multipleSnapshots() {
        //arrange
        List<AgentSnapshotInfo> infos = TestUtils.createAgentSnapshotInfos(100);

        //act
        for (AgentSnapshotInfo info : infos) {
            testObject.add(info);
        }

        //assert
        testObject.encodeSnapshot();
        byte[] snapshots = testObject.getEncodedMessage();
        ByteBuffer buffer = ByteBuffer.wrap(snapshots);
        buffer.order(ByteOrder.BIG_ENDIAN);

        //the snapshots should include time, sizeOfPositions and positionX, positionY for each info
        float time = buffer.getFloat();
        assertEquals(testObject.getTime(), time, 0.001);
        float size = buffer.getFloat();
        assertEquals(infos.size() * 2, size, 0.001);

        for (int i = 0; i < infos.size(); i++) {
            AgentSnapshotInfo info = infos.get(i);
            float x = buffer.getFloat();
            assertEquals(info.getEasting(), x, 0.001);
            float y = buffer.getFloat();
            assertEquals(info.getNorthing(), y, 0.001);
            Id id = testObject.getIdForIndex(i);
            assertEquals(info.getId(), id);
        }
        assertEquals(buffer.capacity(), buffer.position());
    }

    @Test
    public void encodeSnapshot() {

        //arrange
        SnapshotLinkWidthCalculator calc = new SnapshotLinkWidthCalculator();
        AgentSnapshotInfoFactory factory = new AgentSnapshotInfoFactory(calc);
        List<AgentSnapshotInfo> infos = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Id<Person> id = Id.createPersonId(i);
            double x = Math.random();
            double y = Math.random();
            AgentSnapshotInfo info = factory.createAgentSnapshotInfo(id, x, y, 0, 0);
            infos.add(info);
        }

        for (AgentSnapshotInfo info : infos) {
            testObject.add(info);
        }

        //act
        testObject.encodeSnapshot();

        //assert
        byte[] snapshots = testObject.getEncodedMessage();
        ByteBuffer buffer = ByteBuffer.wrap(snapshots);
        buffer.order(ByteOrder.BIG_ENDIAN);

        //the snapshots should include time, sizeOfPositions and positionX, positionY for each info
        float time = buffer.getFloat();
        assertEquals(testObject.getTime(), time, 0.001);
        float size = buffer.getFloat();
        assertEquals(infos.size() * 2, size, 0.001);

        for (AgentSnapshotInfo info : infos) {
            float x = buffer.getFloat();
            assertEquals(info.getEasting(), x, 0.001);
            float y = buffer.getFloat();
            assertEquals(info.getNorthing(), y, 0.001);
        }
        assertEquals(buffer.capacity(), buffer.position());
    }

}
