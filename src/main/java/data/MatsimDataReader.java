package data;

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
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.QuadTree;

final class MatsimDataReader {

    private String networkFilePath;
    private String eventsFilePath;
    private String populationFilePath;

    private Network rawNetwork;

    private NetworkData networkData;
    private SnapshotData snapshotData;
    private PopulationData populationData;

    MatsimDataReader(String networkFilePath, String eventsFilePath, String populationFilePath) {
        this.networkFilePath = networkFilePath;
        this.eventsFilePath = eventsFilePath;
        this.populationFilePath = populationFilePath;
    }

    NetworkData getNetworkData() {
        return networkData;
    }

    SnapshotData getSnapshotData() {
        return snapshotData;
    }

    PopulationData getPopulationData() {
        return populationData;
    }

    void setRawNetwork(Network network) {
        this.rawNetwork = network;
    }

    void readAllFiles(double snapshotPeriod) {
        initNetworkData();
        initSnapshotData(snapshotPeriod);
        initPopulationData();
    }

    private void initNetworkData() {
        rawNetwork = readNetworkFile();
        networkData = initNetworkData(rawNetwork);
    }

    private NetworkData initNetworkData(Network network) {

        QuadTree.Rect bounds = calculateBoundingRectangle(network);
        NetworkData networkData = new NetworkData(bounds);

        for (final Link link : network.getLinks().values()) {
            networkData.addLink(link);
        }
        return networkData;
    }

    private void initSnapshotData(double snapshotPeriod) {
        snapshotData = readEventsFile(snapshotPeriod);
    }

    private void initPopulationData() {
        Population pop = readPopulationFile();
        populationData = new PopulationData(pop, rawNetwork);
    }

    Network readNetworkFile() {
        Network net = NetworkUtils.createNetwork();
        MatsimNetworkReader reader = new MatsimNetworkReader(net);
        reader.readFile(networkFilePath);
        return net;
    }

    private QuadTree.Rect calculateBoundingRectangle(Network network) {
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

    SnapshotData readEventsFile(double snapshotPeriod) {

        Config config = ConfigUtils.createConfig();
        config.qsim().setSnapshotStyle(QSimConfigGroup.SnapshotStyle.queue);
        SnapshotGenerator generator = new SnapshotGenerator(this.rawNetwork, snapshotPeriod, config.qsim());
        SnapshotWriterImpl writer = new SnapshotWriterImpl(snapshotPeriod);
        generator.addSnapshotWriter(writer);
        EventsManager eventsManager = EventsUtils.createEventsManager();
        eventsManager.addHandler(generator);
        MatsimEventsReader reader = new MatsimEventsReader(eventsManager);
        reader.readFile(eventsFilePath);
        generator.finish();
        return writer.getSimulationData();
    }

    Population readPopulationFile() {

        MutableScenario scenario = (MutableScenario) ScenarioUtils.createScenario(ConfigUtils.createConfig());
        scenario.setNetwork(this.rawNetwork);
        PopulationReader reader = new MatsimPopulationReader(scenario);
        reader.readFile(populationFilePath);
        return scenario.getPopulation();
    }
}