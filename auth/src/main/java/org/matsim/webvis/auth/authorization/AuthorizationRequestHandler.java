package org.matsim.webvis.auth.authorization;


import org.matsim.webvis.auth.Routes;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.user.LoginUserRequestHandler;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorizationRequestHandler implements Route {

    static Map<String, AuthenticationRequest> loginSession = new ConcurrentHashMap<>();

    AuthorizationService authService = AuthorizationService.Instance;
    TokenService tokenService = TokenService.Instance;

    @Override
    public Object handle(Request request, Response response) {

        AuthenticationRequest authRequest;

        if (request.queryMap().hasKeys())
            authRequest = authRequestFromRequest(request);
        else
            authRequest = authRequestFromSession(request);

        try {
            String subjectId = authenticateWithCookie(request);
            return prepareResponse(authRequest, subjectId, response);
        } catch (UnauthorizedException e) {
            return authenticateWithLogin(request, authRequest, response);
        } catch (CodedException e) {
            return redirectOnError(authRequest.getRedirectUri(), e.getErrorCode(), e.getMessage(), response);
        }
    }

    private AuthenticationRequest authRequestFromRequest(Request request) {
        AuthenticationRequest authRequest = new AuthenticationRequest(request.queryMap());
        //authService.validateClient(authRequest);
        return authRequest;
    }

    private AuthenticationRequest authRequestFromSession(Request request) {
        AuthenticationRequest authRequest = loginSession.remove(request.session().id());
        request.session().invalidate();
        if (authRequest == null)
            throw new InvalidInputException("must specify authorization parameters");
        return authRequest;
    }

    private String authenticateWithCookie(Request request) {
        String encodedToken = request.cookie(LoginUserRequestHandler.LOGIN_COOKIE_KEY);
        Token idToken = tokenService.validateToken(encodedToken);
        return idToken.getSubjectId();
    }

    private Object authenticateWithLogin(Request request, AuthenticationRequest authRequest, Response response) {

        request.session(true);
        loginSession.put(request.session().id(), authRequest);
        response.redirect(Routes.LOGIN, 302);
        return response;
    }

    private Object prepareResponse(AuthenticationRequest authRequest, String subjectId, Response response) {

        //URI uri = authService.generateAuthenticationResponse(authRequest, subjectId);
        //response.redirect(uri.toString(), HttpStatus.FOUND);
        //return response;
        return null;
    }

    private Object redirectOnError(URI redirectUri, String code, String message, Response response) {

        //TODO logger.error("Error: " + code + " " + message + "redirecting to: " + redirectUri);
        String redirect = redirectUri.toString() + "?error=" + code + "&error_description=" + message;
        response.redirect(redirect, 302);
        return response;
    }
}