package org.matsim.viz.postprocessing.od;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geotools.referencing.CRS;
import org.junit.Ignore;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.api.experimental.events.TeleportationArrivalEvent;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.NetworkUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacility;
import org.matsim.facilities.FacilitiesUtils;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Testitest {

	private static ActivityFacilities facilities = FacilitiesUtils.createActivityFacilities();

	@Test
	@Ignore
	public void readGeoJson() throws IOException {

		Path geoJson = Paths.get("C:\\Users\\Janek\\Desktop\\geojson test.geojson");
		InputStream stream = Files.newInputStream(geoJson);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JtsModule());
		FeatureCollection featureCollection = mapper.readValue(geoJson.toFile(), FeatureCollection.class);


	}

	@Test
	@Ignore
	public void readGeoJsonAndTransformCoordinates() throws IOException, FactoryException, TransformException {

		Path geoJson = Paths.get("G:\\Users\\Janek\\tubcloud\\geojson test.geojson");
		Path output = Paths.get("G:\\Users\\Janek\\Desktop\\transformed.geojson");


		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JtsModule());
		FeatureCollection featureCollection = mapper.readValue(geoJson.toFile(), FeatureCollection.class);

		CoordinateReferenceSystem wgs84 = CRS.decode("urn:ogc:def:crs:EPSG:3857");

		FeatureCollection trans = featureCollection.transformCollection(wgs84);

		mapper.writeValue(output.toFile(), trans);
	}

	@Test
	@Ignore
	public void runEventHandlerStuff() {

		Path events = Paths.get("C:\\Users\\Janek\\Downloads\\berlin-v5.3-10pct.output_events.xml.gz");

		EventsManager manager = EventsUtils.createEventsManager();
		TripEventHandler handler = new TripEventHandler();
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
	@Ignore
	public void teleportedWalk() {

		Id<Person> person = Id.createPersonId("person");
		Id<ActivityFacility> homeFacility = Id.create("home", ActivityFacility.class);
		Id<ActivityFacility> workFacility = Id.create("work", ActivityFacility.class);
		Id<Link> homeLink = Id.createLinkId(1);
		Id<Link> workLink = Id.createLinkId(2);

		TripEventHandler handler = new TripEventHandler();

		// mock a home -> work -> home plan with teleported walk
		handler.handleEvent(new ActivityEndEvent(0, person, homeLink, homeFacility, "home"));
		handler.handleEvent(new PersonDepartureEvent(0, person, homeLink, "walk"));
		handler.handleEvent(new TeleportationArrivalEvent(30, person, 100));
		handler.handleEvent(new PersonArrivalEvent(30, person, workLink, "walk"));
		handler.handleEvent(new ActivityStartEvent(30, person, workLink, workFacility, "work"));

		handler.handleEvent(new ActivityEndEvent(3600, person, workLink, workFacility, "work"));
		handler.handleEvent(new PersonDepartureEvent(3600, person, workLink, "walk"));
		handler.handleEvent(new TeleportationArrivalEvent(3630, person, 100));
		handler.handleEvent(new PersonArrivalEvent(3630, person, homeLink, "walk"));
		handler.handleEvent(new ActivityStartEvent(3630, person, homeLink, homeFacility, "home"));

		assertTrue(handler.getTripToPerson().containsKey(person));
		List<Trip> trips = handler.getTripToPerson().get(person);
		assertEquals(2, trips.size());

		Trip firstTrip = trips.get(0);
		assertEquals("walk", firstTrip.getMainMode());
		assertEquals(0, firstTrip.getDepartureTime(), 0.0001);
		assertEquals(homeLink, firstTrip.getDepartureLink());
		assertEquals(workLink, firstTrip.getArrivalLink());
		assertEquals(30, firstTrip.getArrivalTime(), 0.0001);
		assertEquals(1, firstTrip.getLegs().size());
		assertTrue(firstTrip.getLegs().get(0).isTeleported());
	}

	private Network createTestNetwork() {

		Network network = NetworkUtils.createNetwork();

		Node n1 = network.getFactory().createNode(Id.createNodeId(1), new Coord(0, 0));
		Node n2 = network.getFactory().createNode(Id.createNodeId(2), new Coord(1000, 0));
		Node n3 = network.getFactory().createNode(Id.createNodeId(2), new Coord(1000, 1000));
		Node n4 = network.getFactory().createNode(Id.createNodeId(2), new Coord(0, 1000));

		network.addNode(n1);
		network.addNode(n2);
		network.addNode(n3);
		network.addNode(n4);

		Link l1 = network.getFactory().createLink(Id.createLinkId(1), n1, n2);
		Link l2 = network.getFactory().createLink(Id.createLinkId(2), n2, n3);
		Link l3 = network.getFactory().createLink(Id.createLinkId(3), n3, n4);
		Link l4 = network.getFactory().createLink(Id.createLinkId(4), n4, n1);

		network.addLink(l1);
		network.addLink(l2);
		network.addLink(l3);
		network.addLink(l4);

		return network;
	}
}
