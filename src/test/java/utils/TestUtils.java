package utils;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfoFactory;
import org.matsim.vis.snapshotwriters.SnapshotLinkWidthCalculator;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    public static final String NETWORK_FILE = "src/test/data/test-network.xml";
    public static final String EVENTS_FILE = "src/test/data/test-events-100.xml.gz";

    public static Network loadTestNetwork() {
        Network network = NetworkUtils.createNetwork();
        MatsimNetworkReader reader = new MatsimNetworkReader(network);
        reader.readFile(NETWORK_FILE);
        return network;
    }

    public static List<AgentSnapshotInfo> createAgentSnapshotInfos(int numberOfInfos) {

        List<AgentSnapshotInfo> infos = new ArrayList<>();

        for (int i = 0; i < numberOfInfos; i++) {
            double x = Math.random();
            double y = Math.random();
            infos.add(createAgentSnapshotInfo(i, x, y));
        }
        return infos;
    }

    public static AgentSnapshotInfo createAgentSnapshotInfo(long key, double easting, double northing) {
        SnapshotLinkWidthCalculator calc = new SnapshotLinkWidthCalculator();
        AgentSnapshotInfoFactory factory = new AgentSnapshotInfoFactory(calc);
        Id<Person> id = Id.createPersonId(1);
        return factory.createAgentSnapshotInfo(id, easting, northing, 0, 0);
    }
}
