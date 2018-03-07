package authorization;

import data.entities.User;
import requests.ErrorCode;
import requests.RequestException;
import spark.*;
import spark.template.mustache.MustacheTemplateEngine;
import token.TokenService;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorizationRequestHandler implements Route {

    private static Map<String, AuthenticationRequest> loginSession = new ConcurrentHashMap<>();

    private TokenService tokenService = new TokenService();
    private AuthorizationService authService = new AuthorizationService();

    public AuthorizationRequestHandler() throws UnsupportedEncodingException {
    }

    private static String renderLogin() {
        return new MustacheTemplateEngine().render(new ModelAndView(new HashMap<>(), "login.mustache"));
    }

    @Override
    public Object handle(Request request, Response response) {

        AuthenticationRequest authRequest = null;

        try {
            authRequest = parse(request);
        } catch (URIException e) {
            return errorResponse(ErrorCode.INVALID_REQUEST, "redirect_uri missing or malformed");
        } catch (RequestException e) {
            return redirectOnError(authRequest.getRedirectUri(), e.getErrorCode(), e.getMessage(), response);
        }

        if (!authService.isValidClientInformation(authRequest)) {
            return redirectOnError(authRequest.getRedirectUri(), ErrorCode.UNAUTHORIZED_CLIENT,
                                   "client was not registered or redirect url was not registered", response);
        }

        User user;
        try {
            //now, that we have the parameters, check whether the user is actually logged in
            String token = request.cookie("id_token");
            user = tokenService.validateIdToken(token);
        } catch (Exception e) {

            //if the user is not logged in, go to the login screen
            request.session(true);
            loginSession.put(request.session().id(), authRequest);
            return renderLogin();
        }

        //delete login session
        request.session().invalidate();

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

    private Object redirectOnError(URI redirectUri, String code, String message, Response response) {
        String redirect = redirectUri.toString() + "?error=" + code + "&error_description=" + message;
        response.redirect(redirect, 302);
        return response;
    }
}
