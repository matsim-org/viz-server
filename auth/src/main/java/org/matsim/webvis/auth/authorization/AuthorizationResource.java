package org.matsim.webvis.auth.authorization;

import io.dropwizard.jersey.sessions.Session;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/authorize")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorizationResource {

    private static Map<String, AuthRequest> loginSession = new ConcurrentHashMap<>();

    private TokenService tokenService = TokenService.Instance;
    private AuthorizationService authService = AuthorizationService.Instance;

    @GET
    public Response authorize(
            @Valid @BeanParam AuthGetRequest request,
            @Session HttpSession session,
            @CookieParam("login") String token) {

        return doAuthorization(request, session, token);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authorize(
            @Valid @BeanParam AuthPostRequest request,
            @Session HttpSession session,
            @CookieParam("login") String token) {

        return doAuthorization(request, session, token);
    }

    @GET
    @Path("/from-login")
    public Response afterLogin(@Session HttpSession session, @CookieParam("login") String token) {

        AuthRequest request = loginSession.get(session.getId());
        session.invalidate();
        if (request == null)
            throw new InvalidInputException("call '/authorize' with proper parameters");
        return doAuthorization(request, session, token);
    }

    private Response doAuthorization(AuthRequest request, HttpSession session, String token) {

        request.validate();
        authService.validateClient(request);

        try {
            String subjectId = authenticateWithCookie(token);
            return prepareResponse(request, subjectId);
        } catch (UnauthorizedException e) {
            return authenticateWithLogin(request, session);
        } catch (CodedException e) {
            return redirectOnError(request.getRedirectUri(), e.getErrorCode(), e.getMessage());
        }
    }

    private String authenticateWithCookie(String token) {

        Token idToken = tokenService.validateToken(token);
        return idToken.getSubjectId();
    }

    private Response authenticateWithLogin(AuthRequest request, HttpSession session) {

        loginSession.put(session.getId(), request);
        return Response.seeOther(URI.create("/login")).build();
    }

    private Response prepareResponse(AuthRequest request, String subjectId) {

        URI uri = authService.generateAuthenticationResponse(request, subjectId);
        return Response.seeOther(uri).build();
    }

    private Response redirectOnError(URI redirectURI, String code, String message) {

        redirectURI = redirectURI.resolve("?error=" + code + "&error_description=" + message);
        return Response.seeOther(redirectURI).build();
    }
}
