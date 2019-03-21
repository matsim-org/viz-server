package org.matsim.viz.postprocessing.eventAnimation;

import lombok.val;
import org.hibernate.Session;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.viz.postprocessing.bundle.VisualizationGenerator;

import java.nio.file.Path;

public class DataGenerator implements VisualizationGenerator<Visualization> {

	private static String NETWORK_KEY = "network";
	private static String EVENTS_KEY = "events";

	private Network originalNetwork;

	@Override
	public Visualization createVisualization() {
		return new Visualization();
	}

	@Override
	public void generate(Input<Visualization> input) {

		val networkPath = input.getInputFiles().get(NETWORK_KEY).getPath();
		val eventsPath = input.getInputFiles().get(EVENTS_KEY).getPath();

		try (val session = input.getSessionFactory().openSession()) {

			generateNetwork(networkPath, input.getVisualization(), session);
			generateLinkTrips(eventsPath, input.getVisualization(), session);
		}
	}

	private void generateNetwork(Path networkPath, Visualization visualization, Session session) {

		originalNetwork = NetworkUtils.createNetwork();
		new MatsimNetworkReader(originalNetwork).readFile(networkPath.toString());
		val matsimNetwork = new MatsimNetwork(originalNetwork);

		session.beginTransaction();
		session.persist(matsimNetwork);
		session.merge(visualization);
		matsimNetwork.setVisualization(visualization);
		visualization.setMatsimNetwork(matsimNetwork);
		session.getTransaction().commit();
	}

	private void generateLinkTrips(Path eventsPath, Visualization visualization, Session session) {

		val handler = new EventsHandler(originalNetwork, session);
		val eventsManager = EventsUtils.createEventsManager();
		eventsManager.addHandler(handler);
		val reader = new MatsimEventsReader(eventsManager);

		reader.readFile(eventsPath.toString());
	}
}
