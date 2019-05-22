package org.matsim.viz.postprocessing.od;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

import java.util.List;

public class RoutedLeg extends Leg {

	private List<Id<Link>> usedLinks;
}
