package org.matsim.webvis.auth.token;

import io.dropwizard.auth.Auth;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/introspect")
@Produces(MediaType.APPLICATION_JSON)
public class IntrospectResource {

    RelyingPartyService rpService = RelyingPartyService.Instance;
    private TokenService tokenService = TokenService.Instance;

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
