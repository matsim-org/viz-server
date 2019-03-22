package org.matsim.viz.postprocessing.eventAnimation;

import lombok.Getter;
import lombok.extern.java.Log;
import lombok.val;
import org.hibernate.Session;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.VehicleEntersTrafficEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.vehicles.Vehicle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log
public class EventsHandler implements AutoCloseable, LinkEnterEventHandler, LinkLeaveEventHandler, VehicleEntersTrafficEventHandler {

	private static final int batchSize = 50;
	private final Network network;
	private final Session session;
	private final Visualization visualization;

	@Getter
	private Map<String, LinkTrip> linkTrips = new ConcurrentHashMap<>();
	private int batchCount = 0;

	EventsHandler(Visualization visualization, Network network, Session session) {

		this.visualization = visualization;
		this.network = network;
		this.session = session;
		this.session.setJdbcBatchSize(batchSize);
		this.session.beginTransaction();
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {

		handleEnterEvent(event.getTime(), event.getLinkId(), event.getVehicleId());
	}

	@Override
	public void handleEvent(VehicleEntersTrafficEvent event) {

		handleEnterEvent(event.getTime(), event.getLinkId(), event.getVehicleId());
	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {

		setFirstOrLastTimestep(event.getTime());
		String tripKey = getLinkTripsKey(event.getVehicleId(), event.getLinkId());
		if (linkTrips.containsKey(tripKey)) {
			val linkTrip = linkTrips.remove(tripKey);
			linkTrip.setLeaveTime(event.getTime());
			session.persist(linkTrip);

			if (++batchCount % batchSize == 0) {
				log.info("Batch count is: " + batchCount + ". Flushing to database.");
				session.flush();
				session.clear();
			}
		} else {
			log.warning("There was no LinkTrip saved for: " + tripKey);
		}
	}

	@Override
	public void close() {

		session.merge(visualization);
		session.getTransaction().commit();
	}

	private void handleEnterEvent(double time, Id<Link> linkId, Id<Vehicle> vehicleId) {

		val link = network.getLinks().get(linkId);
		val linkTrip = createLinkTrip(time, link);
		linkTrips.put(getLinkTripsKey(vehicleId, linkId), linkTrip);
		setFirstOrLastTimestep(time);
	}

	private LinkTrip createLinkTrip(double time, Link link) {
		// TODO: make this constructor less verbose
		return new LinkTrip(
				time, 0,
				link.getFromNode().getCoord().getX(), link.getFromNode().getCoord().getY(),
				link.getToNode().getCoord().getX(), link.getToNode().getCoord().getY(),
				visualization
		);
	}

	private String getLinkTripsKey(Id<Vehicle> vehicleId, Id<Link> linkId) {
		return vehicleId.toString() + linkId.toString();
	}

	private void setFirstOrLastTimestep(double timestep) {
		if (timestep < visualization.getFirstTimestep())
			visualization.setFirstTimestep(timestep);
		if (timestep > visualization.getLastTimestep()) {
			visualization.setLastTimestep(timestep);
		}
	}
}
