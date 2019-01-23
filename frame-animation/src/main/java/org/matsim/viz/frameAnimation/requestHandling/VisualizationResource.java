package org.matsim.viz.frameAnimation.requestHandling;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.matsim.viz.error.ForbiddenException;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.viz.frameAnimation.persistenceModel.*;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;

@RequiredArgsConstructor
@Path("{id}")
public class VisualizationResource {

    private final EntityManagerFactory emFactory;

    @GET
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public ConfigurationResponse configuration(@Auth Agent agent, @PathParam("id") String vizId) {

        val visualization = findVisualization(agent, vizId);

        if (visualization.getProgress() == Visualization.Progress.Done)
            return ConfigurationResponse.createFromVisualization(visualization);
        else
            return ConfigurationResponse.createForProgressNotDone(visualization.getProgress());
    }

    @GET
    @Path("/network")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @UnitOfWork
    public byte[] network(@Auth Agent agent, @PathParam("id") String vizId) {

        if (hasNoPermission(agent, vizId))
            throw new ForbiddenException("user doesn't have permission");

        val networkTable = QMatsimNetwork.matsimNetwork;
        val result = new JPAQueryFactory(emFactory.createEntityManager()).selectFrom(networkTable)
                .where(networkTable.visualization.filesServerId.eq(vizId))
                .fetchFirst();
        return result.getData();
    }

    @GET
    @Path("/snapshots")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @UnitOfWork
    public Response snapshots(@Auth Agent agent,
                              @PathParam("id") String vizId,
                              @QueryParam("fromTimestep") double fromTimestep,
                              @QueryParam("numberOfTimesteps") int numberOfTimesteps,
                              @QueryParam("speedFactor") double speedFactor) {

        val visualization = findVisualization(agent, vizId);
        val toTimestep = fromTimestep + numberOfTimesteps * visualization.getTimestepSize();

        QSnapshot snapshotTable = QSnapshot.snapshot;
        val snapshots = new JPAQueryFactory(emFactory.createEntityManager()).selectFrom(snapshotTable)
                .where(snapshotTable.visualization.filesServerId.eq(vizId)
                        .and(snapshotTable.timestep.between(fromTimestep, toTimestep))
                ).fetch();

        return Response.ok((StreamingOutput) outputStream -> {

            try {
                for (val snapshot : snapshots) {
                    outputStream.write(snapshot.getData());
                    outputStream.flush();
                }
            } catch (IOException e) {
                throw new InternalException("An error occurred while processing the request");
            } finally {
                outputStream.close();
            }
        }).build();
    }

    @GET
    @Path("/plan")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public String plan(@Auth Agent agent,
                       @PathParam("id") String vizId,
                       @QueryParam("index") int index) {

        if (hasNoPermission(agent, vizId))
            throw new ForbiddenException("user doesn't have permission");

        val planTable = QPlan.plan;
        val plan = new JPAQueryFactory(emFactory.createEntityManager()).selectFrom(planTable)
                .where(planTable.visualization.filesServerId.eq(vizId).and(planTable.idIndex.eq(index)))
                .fetchFirst();
        return plan.getGeoJson();
    }

    private boolean hasNoPermission(Agent agent, String vizId) {

        val permissionTable = QPermission.permission;
        val permission = new JPAQueryFactory(emFactory.createEntityManager()).selectFrom(permissionTable)
                .where(permissionTable.agent.authId.eq(agent.getAuthId()).or(permissionTable.agent.authId.eq(Agent.publicPermissionId)).and(permissionTable.visualization.filesServerId.eq(vizId)))
                .fetchFirst();
        return permission != null;
    }

    private Visualization findVisualization(Agent agent, String vizId) {

        if (hasNoPermission(agent, vizId))
            throw new ForbiddenException("user doesn't have permission");

        QVisualization visualizationTable = QVisualization.visualization;
        val visualization = new JPAQueryFactory(emFactory.createEntityManager()).selectFrom(visualizationTable)
                .where(visualizationTable.filesServerId.eq(vizId))
                .fetchOne();

        if (visualization == null)
            throw new InvalidInputException("Could not find visualization with id: " + vizId);

        return visualization;
    }
}
