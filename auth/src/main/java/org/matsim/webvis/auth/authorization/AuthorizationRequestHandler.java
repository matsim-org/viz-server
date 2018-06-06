package org.matsim.webvis.auth.authorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.Routes;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.user.LoginUserRequestHandler;
import org.matsim.webvis.auth.user.UserService;
import org.matsim.webvis.common.errorHandling.CodedException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorizationRequestHandler implements Route {

    private static Logger logger = LogManager.getLogger();
    private static Map<String, AuthenticationRequest> loginSession = new ConcurrentHashMap<>();

    AuthorizationService authService = AuthorizationService.Instance;
    TokenService tokenService = TokenService.Instance;
    UserService userService = UserService.Instance;

    @Override
    public Object handle(Request request, Response response) {

        if (request.queryMap().hasKeys())
            return authenticate(request, response);
        else
            return prepareResponse(request, response);
    }

    private Object authenticate(Request request, Response response) {

        AuthenticationRequest authRequest = new AuthenticationRequest(request.queryMap());
        authService.validateClient(authRequest);

        request.session(true);
        loginSession.put(request.session().id(), authRequest);
        return redirectToLogin(response);
    }

    private Object prepareResponse(Request request, Response response) {

        String encodedToken = request.cookie(LoginUserRequestHandler.LOGIN_COOKIE_KEY);

        AuthenticationRequest authRequest = loginSession.remove(request.session().id());
        request.session().invalidate();

        try {
            Token idToken = tokenService.validateToken(encodedToken);
            URI uri = authService.generateAuthenticationResponse(authRequest, idToken.getSubjectId());
            response.redirect(uri.toString(), 302);
            return response;
        } catch (CodedException e) {
            return redirectOnError(authRequest.getRedirectUri(), e.getErrorCode(), e.getMessage(), response);
        }
    }

    private Object redirectToLogin(Response response) {
        response.redirect(Routes.LOGIN, 302);
        return response;
    }

    private Object redirectOnError(URI redirectUri, String code, String message, Response response) {

        logger.error("Error: " + code + " " + message + "redirecting to: " + redirectUri);
        String redirect = redirectUri.toString() + "?error=" + code + "&error_description=" + message;
        response.redirect(redirect, 302);
        return response;
    }
}