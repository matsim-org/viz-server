package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.Builder;
import lombok.Getter;
import lombok.val;
import org.hibernate.SessionFactory;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.SnapshotGenerator;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.viz.frameAnimation.persistenceModel.MatsimNetwork;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;

import java.nio.file.Path;

class VisualizationProcessor {

    private Path networkFilePath;
    private Path eventsFilePath;
    private Path populationFilePath;

    private double snapshotPeriod;
    private SessionFactory sessionFactory;

    private Network originalNetwork;

    @Getter
    private MatsimNetwork generatedNetwork;

    @Builder
    VisualizationProcessor(Path network, Path events, Path population, double snapshotPeriod, SessionFactory sessionFactory) {
        this.networkFilePath = network;
        this.eventsFilePath = events;
        this.populationFilePath = population;
        this.sessionFactory = sessionFactory;
        this.snapshotPeriod = snapshotPeriod;
    }

    void readNetwork(Visualization visualization) {

        originalNetwork = NetworkUtils.createNetwork();
        new MatsimNetworkReader(originalNetwork).readFile(networkFilePath.toString());

        val networkEntity = new MatsimNetwork(originalNetwork);
        visualization.addNetwork(networkEntity);
        val session = sessionFactory.getCurrentSession();
        val transaction = session.beginTransaction();
        try {
            session.save(visualization);
            transaction.commit();
            this.generatedNetwork = networkEntity;
        } catch (Exception e) {
            throw new RuntimeException("Could not persist network");
        }
    }

    void readEvents(Visualization visualization) {
        Config config = ConfigUtils.createConfig();
        config.qsim().setSnapshotStyle(QSimConfigGroup.SnapshotStyle.queue);
        SnapshotGenerator generator = new SnapshotGenerator(this.originalNetwork, snapshotPeriod, config.qsim());
        val writer = new DatabaseSnapshotWriter(visualization, this.sessionFactory);
        generator.addSnapshotWriter(writer);
        val eventsManager = EventsUtils.createEventsManager();
        eventsManager.addHandler(generator);
        val reader = new MatsimEventsReader(eventsManager);

        // this is the part where database action happens
        try {
            reader.readFile(eventsFilePath.toString());
        } catch (Exception e) {
            throw new RuntimeException("an error occurred while writing snapshots");
        } finally {
            // database snapshot writer opens a db session it must be closed
            generator.finish();
        }
    }

    void readPopulation(Visualization visualization) {

        val scenario = ScenarioUtils.createMutableScenario(ConfigUtils.createConfig());
        scenario.setNetwork(this.originalNetwork);
        val reader = new PopulationReader(scenario);
        reader.readFile(populationFilePath.toString());

    }
}
