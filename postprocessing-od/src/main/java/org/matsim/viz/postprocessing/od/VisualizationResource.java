package org.matsim.viz.postprocessing.od;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.hibernate.SessionFactory;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.postprocessing.bundle.Agent;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.nio.file.Files;
import java.util.Set;

@Path("{id}")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class VisualizationResource {

	private final SessionFactory sessionFactory;
	private final java.nio.file.Path jsonPath;

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("cells")
	@UnitOfWork
	public Response cells(@Auth Agent agent, @PathParam("id") String vizId) {

		Visualization visualization = sessionFactory.getCurrentSession().find(Visualization.class, vizId);

		if (visualization == null)
			throw new InvalidInputException("Could not find visualization with id: " + vizId);

		String filename = vizId + ".geojson";
		java.nio.file.Path geoJson = jsonPath.resolve(filename);
		return Response.ok((StreamingOutput) output -> IOUtils.copy(Files.newInputStream(geoJson), output))
				.type(MediaType.APPLICATION_JSON)
				.header("Content-Disposition", "attachment; filename=" + filename)
				.build();
	}

	@GET
	@Path("relations")
	@UnitOfWork
	public Set<ODRelation> relations(@Auth Agent agent, @PathParam("id") String vizId) {

		Visualization visualization = sessionFactory.getCurrentSession().find(Visualization.class, vizId);
		if (visualization == null)
			throw new InvalidInputException("Could not find visualization with id: " + vizId);
		return visualization.getOdRelations();
	}

}
