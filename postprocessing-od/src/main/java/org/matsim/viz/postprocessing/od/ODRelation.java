/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.viz.postprocessing.od;

/**
 * @author ikaddoura
 */

public class ODRelation {

	private final String odId;
	private final String origin;
	private final String destination;
	private double trips;

	public ODRelation(String odId, String origin, String destination) {
		this.odId = odId;
		this.origin = origin;
		this.destination = destination;
		this.trips = 1;
	}

	public String getOdId() {
		return odId;
	}

	public String getOrigin() {
		return origin;
	}

	public String getDestination() {
		return destination;
	}

	public Double getTrips() {
		return trips;
	}

	public void setTrips(double trips) {
		this.trips = trips;
	}

}

