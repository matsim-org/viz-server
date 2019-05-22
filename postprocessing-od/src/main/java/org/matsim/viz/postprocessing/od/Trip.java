package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import lombok.Setter;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
class Trip {

	private Id<Link> departureLink;
	private Id<Link> arrivalLink;

	private double departureTime;
	private double arrivalTime;

	private String mainMode = TransportMode.other;

	private List<Leg> legs = new ArrayList<>();

	Trip(double departureTime, Id<Link> departureLink) {
		this.departureLink = departureLink;
		this.departureTime = departureTime;
	}

	void addLeg(Leg leg) {

		this.mainMode = MainMode.getHigherRankinMode(mainMode, leg.getMode());
		this.legs.add(leg);
	}
}
