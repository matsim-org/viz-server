package org.matsim.viz.postprocessing.eventAnimation;

import lombok.Getter;
import lombok.extern.java.Log;
import lombok.val;
import org.hibernate.Session;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.vehicles.Vehicle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log
public class EventsHandler implements LinkEnterEventHandler, LinkLeaveEventHandler {

	private static final int batchSize = 100;
	private final Network network;
	private final Session session;
	@Getter
	private Map<String, LinkTrip> linkTrips = new ConcurrentHashMap<>();
	private int batchCount = 0;

	public EventsHandler(Network network, Session session) {

		this.network = network;
		this.session = session;
		this.session.setJdbcBatchSize(batchSize);
		this.session.beginTransaction();
	}

	@Override
	public void handleEvent(LinkEnterEvent linkEnterEvent) {

		val linkId = linkEnterEvent.getLinkId();
		val link = network.getLinks().get(linkId);
		val vehicleId = linkEnterEvent.getVehicleId();

		// TODO: make this constructor less verbose
		val linkTrip = new LinkTrip(
				linkEnterEvent.getTime(), link.getFromNode().getCoord().getX(), link.getFromNode().getCoord().getY(),
				link.getToNode().getCoord().getX(), link.getToNode().getCoord().getY(),
				vehicleId.toString(), linkId.toString()
		);
		linkTrips.put(getLinkTripsKey(vehicleId, linkId), linkTrip);
	}

	@Override
	public void handleEvent(LinkLeaveEvent linkLeaveEvent) {


		val linkTrip = linkTrips.remove(getLinkTripsKey(linkLeaveEvent.getVehicleId(), linkLeaveEvent.getLinkId()));
		linkTrip.setLeaveTime(linkLeaveEvent.getTime());
		session.persist(linkTrip);

		if (++batchCount % batchSize == 0) {
			session.flush();
			session.clear();
		}
	}

	private String getLinkTripsKey(Id<Vehicle> vehicleId, Id<Link> linkId) {
		return vehicleId.toString() + linkId.toString();
	}
}
