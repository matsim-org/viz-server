package org.matsim.viz.auth.token;

import io.dropwizard.auth.Auth;
import org.matsim.viz.auth.entities.RelyingParty;
import org.matsim.viz.auth.entities.Token;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/introspect")
@Produces(MediaType.APPLICATION_JSON)
public class IntrospectResource {

    TokenService tokenService = TokenService.Instance;

    @GET
    public IntrospectionResponse introspect(@Auth RelyingParty rp, @QueryParam("token") String token) {

        try {
            Token result = tokenService.validateToken(token);
            return new ActiveIntrospectionResponse(result);
        } catch (RuntimeException e) {
            return new InactiveIntrospectionResponse();
        }
    }
}
