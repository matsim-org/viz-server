package org.matsim.viz.postprocessing.eventAnimation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class LinkTripResponse {

	private double enterTime;
	private double leaveTime;
	private double fromX;
	private double fromY;
	private double toX;
	private double toY;

	static LinkTripResponse fromLinkTrip(LinkTrip trip) {
		return new LinkTripResponse(
				trip.getEnterTime(), trip.getLeaveTime(),
				trip.getFromX(), trip.getFromY(),
				trip.getToX(), trip.getToY()
		);
	}

}
