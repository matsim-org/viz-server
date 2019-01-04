package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.Builder;
import lombok.val;
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

import javax.persistence.EntityManagerFactory;
import java.nio.file.Path;
import java.util.List;

class VisualizationProcessor {

    private final Path networkFilePath;
    private final Path eventsFilePath;
    private final Path populationFilePath;
    private final Visualization visualization;

    private final EntityManagerFactory emFactory;

    private Network originalNetwork;
    private List<Id> idMapping; // TODO use idMapping in Population reader

    @Builder
    VisualizationProcessor(Path network, Path events, Path population, Visualization visualization, EntityManagerFactory emFactory) {
        this.networkFilePath = network;
        this.eventsFilePath = events;
        this.populationFilePath = population;
        this.emFactory = emFactory;
        this.visualization = visualization;
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
        val em = emFactory.createEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(visualization);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    private void readEvents(Visualization visualization) {
        Config config = ConfigUtils.createConfig();
        config.qsim().setSnapshotStyle(QSimConfigGroup.SnapshotStyle.queue);
        SnapshotGenerator generator = new SnapshotGenerator(this.originalNetwork, visualization.getTimestepSize(), config.qsim());
        val writer = new DatabaseSnapshotWriter(visualization, this.emFactory);
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

        val writer = new DatabasePopulationWriter(populationFilePath, this.originalNetwork, emFactory, visualization);
        writer.readPopulationAndWriteToDatabase();
    }
}
