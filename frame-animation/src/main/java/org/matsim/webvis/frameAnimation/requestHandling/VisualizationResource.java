package org.matsim.webvis.frameAnimation.requestHandling;

import org.geojson.FeatureCollection;
import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.webvis.frameAnimation.data.DataProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Path("{id}")
public class VisualizationResource {

    private final DataProvider data = DataProvider.Instance;

    @GET
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationResponse configuration(@PathParam("id") String vizId) {

        return data.getConfiguration(vizId);
    }

    @GET
    @Path("/network")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] network(@PathParam("id") String vizId) {
        return data.getLinks(vizId);
    }

    @GET
    @Path("/snapshots")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response snapshots(@PathParam("id") String vizId,
                              @QueryParam("fromTimestep") double fromTimestep,
                              @QueryParam("numberOfTimesteps") int numberOfTimesteps,
                              @QueryParam("speedFactor") double speedFactor) {
        try {

            ByteArrayOutputStream snapshots = data.getSnapshots(vizId, fromTimestep, numberOfTimesteps, speedFactor);

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
    public FeatureCollection plan(@PathParam("id") String vizId,
                                  @QueryParam("index") int index) {

        return data.getPlan(vizId, index);
    }
}
