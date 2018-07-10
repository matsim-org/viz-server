package org.matsim.webvis.frameAnimation.requestHandling;

import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.webvis.frameAnimation.contracts.geoJSON.FeatureCollection;
import org.matsim.webvis.frameAnimation.data.SimulationDataDAO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("{id}")
public class VisualizationResource {

    private final SimulationDataDAO data = SimulationDataDAO.Instance;

    @GET
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    public ConfigurationResponse configuration(@PathParam("id") String vizId) {

        return new ConfigurationResponse(
                data.getBounds(vizId),
                data.getFirstTimestep(vizId),
                data.getLastTimestep(vizId),
                data.getTimestepSize(vizId)
        );
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
    public byte[] snapshots(@PathParam("id") String vizId,
                            @QueryParam("fromTimestep") double fromTimestep,
                            @QueryParam("numberOfTimestepes") int numberOfTimesteps,
                            @QueryParam("speedFactor") double speedFactor) {
        try {
            return data.getSnapshots(vizId, fromTimestep, numberOfTimesteps, speedFactor);
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
