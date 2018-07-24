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
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class VisualizationResource {

    private static final Date minDate = new Date(Long.MIN_VALUE);

    VisualizationService visualizationService = VisualizationService.Instance;

    @GET
    @Path("/visualizations")
    public List<Visualization> findByType(
            @Auth Agent agent,
            @NotEmpty @QueryParam("type") String type,
            @QueryParam("after") String after
    ) {

        Instant afterInstant = Instant.EPOCH;

        if (after != null) {
            Instant parsed = Instant.parse(after);
            if (parsed.isAfter(afterInstant))
                afterInstant = parsed;
        }

        return visualizationService.findByType(type, afterInstant, agent);
    }

    @GET
    @Path("/visualization-types")
    public List<VisualizationType> types() {
        return visualizationService.findAllTypes();
    }

}