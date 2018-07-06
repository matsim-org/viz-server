package org.matsim.webvis.files.visualization;

import io.dropwizard.auth.Auth;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.Visualization;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectVisualizationResource {

    private final VisualizationService visualizationService = VisualizationService.Instance;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Visualization createVisualization(
            @Auth Agent agent,
            @Valid CreateVisualizationRequest request) {

        return visualizationService.createVisualizationFromRequest(request, agent);
    }

    @GET
    @Path("{vizId}")
    public Visualization getVisualization(
            @Auth Agent agent,
            @PathParam("vizId") String vizId
    ) {
        return visualizationService.find(vizId, agent);
    }
}
