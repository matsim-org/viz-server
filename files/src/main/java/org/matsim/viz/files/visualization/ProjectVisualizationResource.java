package org.matsim.viz.files.visualization;

import io.dropwizard.auth.Auth;
import lombok.AllArgsConstructor;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.Visualization;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class ProjectVisualizationResource {

    private final VisualizationService visualizationService;

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

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{vizId}")
    public Response deleteVisualization(@Auth Agent agent, @PathParam("vizId") String vizId) {
        visualizationService.removeVisualization(vizId, agent);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
