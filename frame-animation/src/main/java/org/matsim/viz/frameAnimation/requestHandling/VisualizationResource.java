package org.matsim.viz.frameAnimation.requestHandling;

import io.dropwizard.auth.Auth;
import org.geojson.FeatureCollection;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.viz.frameAnimation.data.DataProvider;
import org.matsim.viz.frameAnimation.entities.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Path("{id}")
public class VisualizationResource {

    private final DataProvider data;

    public VisualizationResource(DataProvider data) {
        this.data = data;
    }

    @GET
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationResponse configuration(@Auth Permission permission, @PathParam("id") String vizId) {

        return data.getConfiguration(vizId, permission);
    }

    @GET
    @Path("/matsimNetwork")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] network(@Auth Permission permission, @PathParam("id") String vizId) {
        return data.getLinks(vizId, permission);
    }

    @GET
    @Path("/snapshots")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response snapshots(@Auth Permission permission,
                              @PathParam("id") String vizId,
                              @QueryParam("fromTimestep") double fromTimestep,
                              @QueryParam("numberOfTimesteps") int numberOfTimesteps,
                              @QueryParam("speedFactor") double speedFactor) {
        try {

            ByteArrayOutputStream snapshots = data.getSnapshots(vizId, fromTimestep, numberOfTimesteps, speedFactor, permission);

            return Response.ok((StreamingOutput) outputStream -> {
                snapshots.writeTo(outputStream);
                outputStream.flush();
                outputStream.close();
            }).build();

        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException("Could not read snapshots");
        }
    }

    @GET
    @Path("/plan")
    @Produces(MediaType.APPLICATION_JSON)
    public FeatureCollection plan(@Auth Permission permission,
                                  @PathParam("id") String vizId,
                                  @QueryParam("index") int index) {

        return data.getPlan(vizId, index, permission);
    }
}
