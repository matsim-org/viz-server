package org.matsim.webvis.files.visualization;

import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.Visualization;
import org.matsim.webvis.files.entities.VisualizationType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class VisualizationResource {

    VisualizationService visualizationService = VisualizationService.Instance;

    @GET
    @Path("/visualizations")
    public List<Visualization> findByType(
            @Auth Agent agent,
            @NotEmpty @QueryParam("type") String type) {
        return visualizationService.findByType(type, agent);
    }

    @GET
    @Path("/visualization-types")
    public List<VisualizationType> types() {
        return visualizationService.findAllTypes();
    }

}
