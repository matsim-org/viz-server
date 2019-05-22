package org.matsim.viz.postprocessing.od;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

public class Testitest {

	@Test
	public void readGeoJson() throws IOException {

		Path geoJson = Paths.get("C:\\Users\\Janek\\Desktop\\geojson test.geojson");
		InputStream stream = Files.newInputStream(geoJson);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JtsModule());
		FeatureCollection featureCollection = mapper.readValue(geoJson.toFile(), FeatureCollection.class);


	}

	@Test
	public void runEventHandlerStuff() {

		Path events = Paths.get("C:\\Users\\Janek\\Downloads\\berlin-v5.3-10pct.output_events.xml.gz");

		EventsManager manager = EventsUtils.createEventsManager();
		EventHandler handler = new EventHandler();
		manager.addHandler(handler);

		new MatsimEventsReader(manager).readFile(events.toString());

		Instant start = Instant.now();
		// now get all the pt trips
		long numberOfPtTrips = handler.getTripToPerson().values().parallelStream()
				.flatMap(Collection::stream)
				.filter(trip -> trip.getMainMode().equals("pt"))
				.count();

		long numberOfCarTrips = handler.getTripToPerson().values().parallelStream()
				.flatMap(Collection::stream)
				.filter(trip -> trip.getMainMode().equals("car"))
				.count();

		long numberOfWalkTrips = handler.getTripToPerson().values().parallelStream()
				.flatMap(Collection::stream)
				.filter(trip -> trip.getMainMode().equals("walk"))
				.count();

		long numberOfAllWalkModeTrips = handler.getTripToPerson().values().parallelStream()
				.flatMap(Collection::stream)
				.filter(trip -> trip.getMainMode().equals("walk") || trip.getMainMode().equals("transit_walk") || trip.getMainMode().equals("access_walk") || trip.getMainMode().equals("egress_walk"))
				.count();

		Duration duration = Duration.between(start, Instant.now());
		System.out.println(duration.getNano());
	}

	@Test
	public void carTripWithAccessEgressWalk() {

		EventHandler handler = new EventHandler();
	}
}
