package org.matsim.viz.postprocessing.eventAnimation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.hibernate.SessionFactory;
import org.matsim.viz.postprocessing.bundle.Agent;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Log
@RequiredArgsConstructor
@Path("{id}")
public class VisualizationResource {

	private final SessionFactory sessionFactory;

	@GET
	@Path("/configuration")
	@Produces(MediaType.APPLICATION_JSON)
	@UnitOfWork
	public ConfigurationResponse configuration(@Auth Agent agent, @PathParam("id") String vizId) {

		val visualization = sessionFactory.getCurrentSession().find(Visualization.class, vizId);
		return ConfigurationResponse.createFromVisualization(visualization);
	}

	@GET
	@Path("/network")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@UnitOfWork
	public byte[] network(@Auth Agent agent, @PathParam("id") String vizId) {

		val qMatsimNetwork = QMatsimNetwork.matsimNetwork;
		val network = new JPAQueryFactory(sessionFactory.getCurrentSession()).selectFrom(qMatsimNetwork)
				.where(qMatsimNetwork.visualization.id.eq(vizId))
				.fetchOne();

		return network.getData();
	}

	@GET
	@Path("linkTrips")
	@Produces(MediaType.APPLICATION_JSON)
	@UnitOfWork
	public List<LinkTrip> linkTrips(@Auth Agent agent, @PathParam("id") String vizId,
									@QueryParam("from") double fromTime, @QueryParam("to") double toTime) {

		val qLinkTrip = QLinkTrip.linkTrip;

		return new JPAQueryFactory(sessionFactory.getCurrentSession()).selectFrom(qLinkTrip)
				.where(qLinkTrip.visualization.id.eq(vizId)
						.and(qLinkTrip.enterTime.between(fromTime, toTime)).or(qLinkTrip.leaveTime.between(fromTime, toTime)))
				.fetch();
	}

	private ConfigurationResponse createResponse(Visualization visualization) {
		return new ConfigurationResponse(
				visualization.getFirstTimestep(), visualization.getLastTimestep(),
				visualization.getMinEasting(), visualization.getMaxEasting(),
				visualization.getMinNorthing(), visualization.getMaxNorthing()
		);
	}
}
