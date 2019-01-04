package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.Builder;
import lombok.Getter;
import lombok.val;
import org.hibernate.SessionFactory;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.SnapshotGenerator;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.viz.frameAnimation.persistenceModel.MatsimNetwork;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;

import java.nio.file.Path;
import java.util.List;

class VisualizationProcessor {

    private final Path networkFilePath;
    private final Path eventsFilePath;
    private final Path populationFilePath;

    private final double snapshotPeriod;
    private final SessionFactory sessionFactory;

    private final Visualization visualization;
    private Network originalNetwork;
    private List<Id> idMapping;

    @Getter
    private MatsimNetwork generatedNetwork;

    @Builder
    VisualizationProcessor(Path network, Path events, Path population, double snapshotPeriod, SessionFactory sessionFactory) {
        this.networkFilePath = network;
        this.eventsFilePath = events;
        this.populationFilePath = population;
        this.sessionFactory = sessionFactory;
        this.snapshotPeriod = snapshotPeriod;

        //TODO this must move somewhere else
        this.visualization = new Visualization();
        visualization.setTimestepSize(snapshotPeriod);
        sessionFactory.getCurrentSession().save(this.visualization);
    }

    void processVisualization() {

        this.readNetwork(visualization);
        this.readEvents(visualization);
        this.readPopulation(visualization);
    }

    private void readNetwork(Visualization visualization) {

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

    private void readEvents(Visualization visualization) {
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
            this.idMapping = writer.getAgentIds();
        } catch (Exception e) {
            throw new RuntimeException("an error occurred while writing snapshots");
        } finally {
            // database snapshot writer opens a db session it must be closed
            generator.finish();
        }
    }

    private void readPopulation(Visualization visualization) {

        val writer = new DatabasePopulationWriter(populationFilePath, this.originalNetwork, sessionFactory, visualization);
        writer.readPopulationAndWriteToDatabase();
    }
}
