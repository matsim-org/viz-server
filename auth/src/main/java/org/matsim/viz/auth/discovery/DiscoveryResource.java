package org.matsim.viz.auth.discovery;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.URI;

@Path("/.well-known")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    private OpenIdConfiguration openIdConfiguration;

    public DiscoveryResource(URI hostURI) {
        this.openIdConfiguration = new OpenIdConfiguration(hostURI);
    }

    @GET
    @Path("/openid-configuration")
    public OpenIdConfiguration getOpenIdConfiguration() {
        return openIdConfiguration;
    }
}
