package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import lombok.Setter;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
class Leg {

	List<Id<Link>> route = new ArrayList<>();

	private double fromTime;
	private double toTime;

	private String mode;
	private boolean isTeleported;

	void addLink(Id<Link> linkId) {
		route.add(linkId);
	}
}
