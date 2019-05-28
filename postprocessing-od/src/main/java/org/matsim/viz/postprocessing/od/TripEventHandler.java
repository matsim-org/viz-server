package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.events.handler.*;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.TeleportationArrivalEvent;
import org.matsim.core.api.experimental.events.handler.TeleportationArrivalEventHandler;
import org.matsim.vehicles.Vehicle;

import java.util.*;

public class TripEventHandler implements PersonStuckEventHandler, LinkLeaveEventHandler, VehicleEntersTrafficEventHandler, TeleportationArrivalEventHandler, TransitDriverStartsEventHandler, ActivityEndEventHandler, ActivityStartEventHandler, PersonDepartureEventHandler, PersonArrivalEventHandler {

	private Set<Id<Person>> ptDrivers = new HashSet<>();
	@Getter
	private Map<Id<Person>, List<Trip>> tripToPerson = new HashMap<>();
	private Map<Id<Vehicle>, Id<Person>> vehicleToPerson = new HashMap<>();
	private Map<Id<Person>, Leg> openLegs = new HashMap<>();

	@Override
	public void handleEvent(TransitDriverStartsEvent event) {

		ptDrivers.add(event.getDriverId());
	}

	@Override
	public void handleEvent(PersonStuckEvent event) {

		tripToPerson.remove(event.getPersonId());
		openLegs.remove(event.getPersonId());
	}

	@Override
	public void handleEvent(ActivityEndEvent event) {
		if (isInteraction(event.getActType()) || ptDrivers.contains(event.getPersonId())) return;

		if (!tripToPerson.containsKey(event.getPersonId())) {
			List<Trip> trips = new ArrayList<>();
			trips.add(new Trip(event.getTime(), event.getLinkId()));
			tripToPerson.put(event.getPersonId(), trips);
		} else {
			tripToPerson.get(event.getPersonId()).add(new Trip(event.getTime(), event.getLinkId()));
		}
	}

	@Override
	public void handleEvent(ActivityStartEvent event) {

		if (isInteraction(event.getActType()) || ptDrivers.contains(event.getPersonId())) return;

		Trip currentTrip = getLastTrip(event.getPersonId());
		currentTrip.setArrivalLink(event.getLinkId());
		currentTrip.setArrivalTime(event.getTime());
	}

	@Override
	public void handleEvent(PersonArrivalEvent event) {

		if (openLegs.containsKey(event.getPersonId())) {
			Leg leg = openLegs.remove(event.getPersonId());
			leg.setToTime(event.getTime());
			leg.addLink(event.getLinkId());
			Trip currentTrip = getLastTrip(event.getPersonId());
			currentTrip.addLeg(leg);
		} else if (!ptDrivers.contains(event.getPersonId())) {
			throw new RuntimeException("Some person is missing.");
		}
	}

	@Override
	public void handleEvent(PersonDepartureEvent event) {

		if (ptDrivers.contains(event.getPersonId())) return;

		Leg leg = new Leg();
		leg.addLink(event.getLinkId());
		leg.setFromTime(event.getTime());
		leg.setMode(event.getLegMode());
		openLegs.put(event.getPersonId(), leg);
	}

	private boolean isInteraction(String type) {
		return type.contains("interaction");
	}

	private Trip getLastTrip(Id<Person> personId) {
		if (tripToPerson.containsKey(personId)) {
			List<Trip> trips = tripToPerson.get(personId);
			return trips.get(trips.size() - 1);
		} else {
			throw new RuntimeException("No trip for person: " + personId.toString());
		}
	}

	@Override
	public void handleEvent(TeleportationArrivalEvent event) {
		if (openLegs.containsKey(event.getPersonId())) {
			openLegs.get(event.getPersonId()).setTeleported(true);
		}
	}

	@Override
	public void handleEvent(VehicleEntersTrafficEvent event) {

		vehicleToPerson.put(event.getVehicleId(), event.getPersonId());
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {

		Id<Person> personId = vehicleToPerson.get(event.getVehicleId());
		if (openLegs.containsKey(personId)) {
			openLegs.get(personId).addLink(event.getLinkId());
		}
	}
}
