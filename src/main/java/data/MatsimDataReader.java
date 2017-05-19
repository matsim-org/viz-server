package data;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.SnapshotGenerator;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.MatsimPopulationReader;
import org.matsim.core.population.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.QuadTree;

public final class MatsimDataReader {

    private static Network cachedNetwork;

    private MatsimDataReader() {
    }

    static NetworkData readNetworkFile(String path) {

        Network network = loadNetworkFile(path);
        return initNetworkData(network);
    }

    private static Network loadNetworkFile(String path) {

        if(cachedNetwork == null) {
            cachedNetwork = NetworkUtils.createNetwork();
            MatsimNetworkReader reader = new MatsimNetworkReader(cachedNetwork);
            reader.readFile(path);
        }
        return cachedNetwork;
    }

    private static NetworkData initNetworkData(Network network) {

        QuadTree.Rect bounds = calculateBoundingRectangle(network);
        NetworkData networkData = new NetworkData(bounds);

        for (final Link link : network.getLinks().values()) {
            networkData.addLink(link);
        }
        return networkData;
    }

    private static QuadTree.Rect calculateBoundingRectangle(Network network) {
        double minEasting = Double.POSITIVE_INFINITY;
        double maxEasting = Double.NEGATIVE_INFINITY;
        double minNorthing = Double.POSITIVE_INFINITY;
        double maxNorthing = Double.NEGATIVE_INFINITY;

        for (Node node : network.getNodes().values()) {
            minEasting = Math.min(minEasting, node.getCoord().getX());
            maxEasting = Math.max(maxEasting, node.getCoord().getX());
            minNorthing = Math.min(minNorthing, node.getCoord().getY());
            maxNorthing = Math.max(maxNorthing, node.getCoord().getY());
        }
        //all nodes should lie within the bounding rectangle
        maxEasting += 1;
        maxNorthing += 1;

        return new QuadTree.Rect(minEasting, minNorthing, maxEasting, maxNorthing);
    }

    public static SnapshotData readEventsFile(String eventsFilePath,
                                              String networkFilePath, double snapshotPeriod) {
        Network network = loadNetworkFile(networkFilePath);
        Config config = ConfigUtils.createConfig();
        config.qsim().setSnapshotStyle(QSimConfigGroup.SnapshotStyle.queue);
        SnapshotGenerator generator = new SnapshotGenerator(network, snapshotPeriod, config.qsim());
        SnapshotWriterImpl writer = new SnapshotWriterImpl(snapshotPeriod);
        generator.addSnapshotWriter(writer);
        EventsManager eventsManager = EventsUtils.createEventsManager();
        eventsManager.addHandler(generator);
        MatsimEventsReader reader = new MatsimEventsReader(eventsManager);
        reader.readFile(eventsFilePath);
        generator.finish();
        return writer.getSimulationData();
    }

    public static PopulationData readPopulationFile(String populationFilePath, String networkFilePath) {
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Network network = loadNetworkFile(networkFilePath);
        PopulationReader reader = new MatsimPopulationReader(scenario);
        reader.readFile(populationFilePath);
        Population population = scenario.getPopulation();
        return new PopulationData(population);
    }
}