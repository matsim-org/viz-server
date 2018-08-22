package org.matsim.webvis.frameAnimation.data;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.SnapshotGenerator;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

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

        NetworkData data = new NetworkData();
        data.addNetwork(network);
        return data;
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

    private SnapshotData readEventsFile(double snapshotPeriod) {

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
        PopulationReader reader = new PopulationReader(scenario);
        reader.readFile(populationFilePath);
        return scenario.getPopulation();
    }
}