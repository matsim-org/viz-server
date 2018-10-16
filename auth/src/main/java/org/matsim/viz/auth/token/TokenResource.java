package org.matsim.viz.auth.token;

import io.dropwizard.auth.Auth;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.matsim.viz.auth.entities.RelyingParty;
import org.matsim.viz.auth.entities.Token;
import org.matsim.viz.error.InvalidInputException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("token")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class TokenResource {

    private final static String GRANT_TYPE = "grant_type";
    private final static String SCOPE = "scope";
    private final static String CLIENT_CREDENTIALS = "client_credentials";

    private TokenService tokenService;

    @POST
    public AccessTokenResponse token(
            @Auth RelyingParty rp,
            @QueryParam(GRANT_TYPE) @NotEmpty String grantType,
            @QueryParam(SCOPE) @NotEmpty String scope
    ) {

        if (!CLIENT_CREDENTIALS.equals(grantType))
            throw new InvalidInputException("unsupported grant type");
        Token token = tokenService.grantForScope(rp, scope);
        return new AccessTokenResponse(token);
    }
}
