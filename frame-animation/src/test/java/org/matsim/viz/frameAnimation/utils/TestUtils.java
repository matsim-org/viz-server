package org.matsim.viz.frameAnimation.utils;

import lombok.val;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfo;
import org.matsim.vis.snapshotwriters.AgentSnapshotInfoFactory;
import org.matsim.vis.snapshotwriters.SnapshotLinkWidthCalculator;
import org.matsim.viz.frameAnimation.config.AppConfiguration;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    public static final String NETWORK_FILE = "test-network.xml";
    public static final String EVENTS_FILE = "test-events-100.xml.gz";
    public static final String POPULATION_FILE = "test-plans-100.xml";

    public static final String NETWORK_FILE_PATH = "src/test/data/test-network.xml";
    public static final String EVENTS_FILE_PATH = "src/test/data/test-events-100.xml.gz";
    public static final String POPULATION_FILE_PATH = "src/test/data/test-plans-100.xml";

    public static Network loadTestNetwork() {
        Network network = NetworkUtils.createNetwork();
        MatsimNetworkReader reader = new MatsimNetworkReader(network);
        reader.readFile(NETWORK_FILE_PATH);
        return network;
    }

    public static Population loadTestPopulation(Network network) {
        val scenario = ScenarioUtils.createMutableScenario(ConfigUtils.createConfig());
        scenario.setNetwork(network);
        val reader = new PopulationReader(scenario);
        reader.readFile(POPULATION_FILE_PATH);
        return scenario.getPopulation();
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
        Id<Person> id = Id.createPersonId(key);
        return factory.createAgentSnapshotInfo(id, easting, northing, 0, 0);
    }

    public static void loadConfig() {
        if (AppConfiguration.getInstance() == null)
            AppConfiguration.setInstance(new AppConfiguration());
    }

    private static String getResourcePath(String resourceFile) throws UnsupportedEncodingException {
        //noinspection ConstantConditions
        return URLDecoder.decode(TestUtils.class.getClassLoader().getResource(resourceFile).getFile(), "UTF-8");
    }
}
