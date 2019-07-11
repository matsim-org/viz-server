package org.matsim.viz.postprocessing.eventAnimation;

import lombok.val;
import org.hibernate.StatelessSession;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.viz.postprocessing.bundle.VisualizationGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

		Path networkPath = input.getInputFiles().get(NETWORK_KEY).getPath();
		Path eventsPath = input.getInputFiles().get(EVENTS_KEY).getPath();

		try (StatelessSession session = input.getSessionFactory().openStatelessSession()) {

			Visualization vizWithNetwork = generateNetwork(networkPath, input.getVisualization(), session);
			//generateLinkTrips(eventsPath, vizWithNetwork, session);
			generateProtoTrips(eventsPath);
		}
	}

	private Visualization generateNetwork(Path networkPath, Visualization visualization, StatelessSession session) {

		originalNetwork = NetworkUtils.createNetwork();
		new MatsimNetworkReader(originalNetwork).readFile(networkPath.toString());
		val matsimNetwork = new MatsimNetwork(originalNetwork);

		session.getTransaction().begin();
		session.insert(matsimNetwork);
		//visualization.addMatsimNetwork(matsimNetwork);
		//session.update(visualization);
		//session.getTransaction().commit();
		return visualization;
	}

	private void generateLinkTrips(Path eventsPath, Visualization visualization, StatelessSession session) {

		try (val handler = new EventsHandler(visualization, originalNetwork, session)) {
			val eventsManager = EventsUtils.createEventsManager();
			eventsManager.addHandler(handler);
			val reader = new MatsimEventsReader(eventsManager);
			reader.readFile(eventsPath.toString());
		}
	}

	private void generateProtoTrips(Path eventsPath) {

		ProtoEventHandler handler = new ProtoEventHandler(originalNetwork);
		val eventsManager = EventsUtils.createEventsManager();
		eventsManager.addHandler(handler);
		new MatsimEventsReader(eventsManager).readFile(eventsPath.toString());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		Path testOutput = Paths.get("G:\\Users\\Janek\\prototest.file");

		try (OutputStream outputStream = Files.newOutputStream(testOutput)) {
			for (LinkTripProto.LinkTrip linkTrip : handler.getResult()) {
				byte[] bytes = linkTrip.toByteArray();
				stream.write(bytes);
				linkTrip.writeDelimitedTo(outputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
