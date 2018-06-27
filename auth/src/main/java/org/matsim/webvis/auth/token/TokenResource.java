package org.matsim.webvis.auth.token;

import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.common.errorHandling.InvalidInputException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("token")
@Produces(MediaType.APPLICATION_JSON)
public class TokenResource {

    private final static String GRANT_TYPE = "grant_type";
    private final static String SCOPE = "scope";
    private final static String CLIENT_CREDENTIALS = "client_credentials";
    private final static String PASSWORD = "password";
    private final static String USERNAME = "username";

    private TokenService tokenService = TokenService.Instance;

    @Path("/client-credentials")
    @POST
    public AccessTokenResponse grantWithClientCredentials(
            @Auth RelyingParty rp,
            @QueryParam(GRANT_TYPE) @NotEmpty String grantType,
            @QueryParam(SCOPE) @NotEmpty String scope
    ) {
        if (!CLIENT_CREDENTIALS.equals(grantType))
            throw new InvalidInputException("parameters don't match grant type");
        Token token = tokenService.grantForScope(rp, scope);
        return new AccessTokenResponse(token);
    }

    @Path("/password")
    @POST
    public AccessTokenResponse grantWithPassword(
            @QueryParam(GRANT_TYPE) @NotEmpty String grantType,
            @QueryParam(SCOPE) @NotEmpty String scope,
            @QueryParam(USERNAME) @NotEmpty String username,
            @QueryParam(PASSWORD) @NotEmpty String password) {

        if (!PASSWORD.equals(grantType))
            throw new InvalidInputException("parameters don't match grant type");
        Token token = tokenService.grantWithPassword(username, password.toCharArray());
        return new AccessTokenResponse(token);
    }
}
