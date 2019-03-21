package org.matsim.viz.postprocessing.eventAnimation;

import io.dropwizard.auth.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.matsim.viz.postprocessing.bundle.Agent;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Log
@RequiredArgsConstructor
@Path("/")
public class VisualizationResource {

	private final EntityManagerFactory emFactory;

	@GET
	public String sayHello(@Auth Agent agent) {
		return "Hello " + agent.getName();
	}
}
