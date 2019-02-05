package org.matsim.viz.postprocessing.emissions;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.postprocessing.emissions.persistenceModel.QPermission;
import org.matsim.viz.postprocessing.emissions.persistenceModel.QVisualization;
import org.matsim.viz.postprocessing.emissions.persistenceModel.Visualization;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@RequiredArgsConstructor
@Path("{id}")
public class VisualizationResource {

    private final EntityManagerFactory emFactory;

    @GET
    @Path("/data")
    @UnitOfWork
    public String data(@Auth Agent agent, @PathParam("id") String vizId) {
        return findVisualization(agent, vizId).getData();
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
                        .or(permissionTable.agent.id.endsWith(Agent.publicPermissionId))
                        .and(permissionTable.visualization.id.eq(vizId)))
                .fetchFirst();
        return permission == null;
    }
}
