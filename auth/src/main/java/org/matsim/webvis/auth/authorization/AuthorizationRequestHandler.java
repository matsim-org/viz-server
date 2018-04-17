package org.matsim.webvis.auth.authorization;

import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.token.TokenService;
import org.matsim.webvis.auth.user.LoginPrompt;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.RequestException;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorizationRequestHandler implements Route {

    private static Map<String, AuthenticationRequest> loginSession = new ConcurrentHashMap<>();

    TokenService tokenService = new TokenService();
    AuthorizationService authService = new AuthorizationService();

    public AuthorizationRequestHandler() throws Exception {
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public Object handle(Request request, Response response) {

        AuthenticationRequest authRequest;

        //parse the request
        try {
            authRequest = parse(request);
        } catch (URIException e) {
            return errorResponse(ErrorCode.INVALID_REQUEST, "redirect_uri missing or malformed");
        } catch (RequestException e) {
            return redirectIfPossible(e.getErrorCode(), e.getMessage(), request, response);
        }

        if (!authService.isValidClientInformation(authRequest)) {
            return errorResponse(ErrorCode.UNAUTHORIZED_CLIENT,
                    "relyingParty was not registered or redirect url was not registered");
        }

        //authenticate org.matsim.webvis.auth.user by org.matsim.webvis.auth.token or by login
        User user;
        try {
            String token = request.cookie("id_token");
            user = tokenService.validateIdToken(token);
        } catch (Exception e) {
            request.session(true);
            loginSession.put(request.session().id(), authRequest);
            return redirectToLogin(response);
        }

        //delete login session
        request.session().invalidate();
        loginSession.remove(authRequest);

        //generate a response
        URI redirect = authService.generateResponse(authRequest, user);
        response.redirect(redirect.toString(), 302);
        return "OK";
    }

    private AuthenticationRequest parse(Request request) throws RequestException, URIException {

        try {
            return new AuthenticationRequest(request.queryMap());
        } catch (RequestException e) {
            Session session = request.session();
            if (session != null && loginSession.containsKey(session.id()))
                return loginSession.get(session.id());
            throw e;
        }
    }

    private Object errorResponse(String code, String message) {
        return "error " + code + " error_description " + message;
    }

    private Object redirectIfPossible(String code, String message, Request request, Response response) {
        if (request.queryMap().hasKey(AuthenticationRequest.REDIRECT_URI))
            return redirectOnError(URI.create(request.queryMap().get(AuthenticationRequest.REDIRECT_URI).value()), code, message, response);
        return errorResponse(code, message);
    }

    private Object redirectOnError(URI redirectUri, String code, String message, Response response) {
        String redirect = redirectUri.toString() + "?error=" + code + "&error_description=" + message;
        response.redirect(redirect, 302);
        return response;
    }

    private Object redirectToLogin(Response response) {
        String redirect = "/login/";
        response.redirect(redirect, 302);
        return response;
    }
}
