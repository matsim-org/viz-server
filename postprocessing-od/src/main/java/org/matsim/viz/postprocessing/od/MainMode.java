package org.matsim.viz.postprocessing.od;

import org.matsim.api.core.v01.TransportMode;

import java.util.HashMap;
import java.util.Map;

public class MainMode {

	private static Map<String, Integer> modes = new HashMap<>();

	static {
		modes.put(TransportMode.pt, 10000);
		modes.put(TransportMode.car, 9000);
		modes.put(TransportMode.ride, 9000);
		modes.put(TransportMode.bike, 8000);
		modes.put(TransportMode.walk, 7000);
		modes.put(TransportMode.transit_walk, 7000);
		modes.put(TransportMode.access_walk, 7000);
		modes.put(TransportMode.egress_walk, 7000);
		modes.put(TransportMode.other, 0);
	}

	public static String getMainMode(Trip trip) {

		String result = TransportMode.other;

		for (Leg leg : trip.getLegs()) {
			int priority = 0;
			if (modes.containsKey(leg.getMode())) {
				priority = modes.get(leg.getMode());
			}
			if (priority > modes.get(result)) {
				result = leg.getMode();
			}
		}
		return result;
	}

	static String getHigherRankinMode(String mode1, String mode2) {

		return modes.getOrDefault(mode1, 0) > modes.getOrDefault(mode2, 0) ? mode1 : mode2;
	}
}
