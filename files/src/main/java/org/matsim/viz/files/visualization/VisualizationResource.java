package org.matsim.viz.files.visualization;

import io.dropwizard.auth.Auth;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.Visualization;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class VisualizationResource {

    private static final Date minDate = new Date(Long.MIN_VALUE);

    private final VisualizationService visualizationService;

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
}
