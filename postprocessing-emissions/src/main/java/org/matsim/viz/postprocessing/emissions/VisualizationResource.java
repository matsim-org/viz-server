package org.matsim.viz.postprocessing.emissions;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.*;
import lombok.extern.java.Log;
import org.matsim.viz.error.ForbiddenException;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.postprocessing.bundle.Agent;
import org.matsim.viz.postprocessing.bundle.QPermission;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static java.lang.Integer.parseInt;

@Data
@Log
@RequiredArgsConstructor
@Path("{id}")
public class VisualizationResource {

    private final EntityManagerFactory emFactory;

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public String data(@Auth Agent agent,
                       @PathParam("id") String vizId,
                       @QueryParam("startTime") String startTime) {

        val bins = findVisualization(agent, vizId).getBins();

        if (startTime == null) return bins.iterator().next().getData();

        int startTimeValue = parseInt(startTime);
        for (Bin bin : bins) {
            if (bin.getStartTime() == startTimeValue) return bin.getData();
        }

        throw new InvalidInputException("Could not find startTime of " + startTime);
    }

    @GET
    @Path("/bins")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public String bins(@Auth Agent agent,
                       @PathParam("id") String vizId) {

        val bins = findVisualization(agent, vizId).getBins();

        String json = "{\"bins\": [";
        for (Bin bin : bins) json = json + bin.getStartTime() + ',';
        json = json.substring(0, json.length() -1 ) + "]}";

        log.info(json);
        return json;
    }

    private Visualization findVisualization(Agent agent, String vizId) {

        if (hasNoPermission(agent, vizId)) {
            throw new ForbiddenException("user doesn't have permission");
        }

        val visualizationTable = QVisualization.visualization;
        val visualization = new JPAQueryFactory(emFactory.createEntityManager()).selectFrom(visualizationTable)
                .where(visualizationTable.id.eq(vizId))
                .fetchOne();

        if (visualization == null)
            throw new InvalidInputException("Could not find visualization with id: " + vizId);
        return visualization;
    }

    private boolean hasNoPermission(Agent agent, String vizId) {

        val permissionTable = QPermission.permission;
        val permission = new JPAQueryFactory(emFactory.createEntityManager()).selectFrom(permissionTable)
                .where(permissionTable.agent.eq(agent)
                        .or(permissionTable.agent.id.endsWith(org.matsim.viz.filesApi.Agent.publicPermissionId))
                        .and(permissionTable.visualization.id.eq(vizId)))
                .fetchFirst();
        return permission == null;
    }
}
