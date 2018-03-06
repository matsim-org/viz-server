package authorization;

import data.entities.AccessToken;
import data.entities.IdToken;
import data.entities.User;
import requests.RequestException;
import spark.*;
import spark.template.mustache.MustacheTemplateEngine;
import token.TokenService;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorizationRequestHandler implements Route {

    private static Map<String, ImplicitAuthorizationRequest> loginSession = new ConcurrentHashMap<>();
    private static Map<String, AuthenticationRequest> authenticationSession = new ConcurrentHashMap<>();
    private TokenService tokenService = new TokenService();

    public AuthorizationRequestHandler() throws UnsupportedEncodingException {
    }

    private static String render(Map<String, Object> model, String path) {
        return new MustacheTemplateEngine().render(new ModelAndView(model, path));
    }

    @Override
    public Object handle(Request request, Response response) {

        AuthenticationRequest authenticationRequest;

        try {
            authenticationRequest = new AuthenticationRequest(request.queryMap());
        } catch (RequestException e) {

            Session session = request.session();

            if (session != null && authenticationSession.containsKey(session.id()))
                authenticationRequest = authenticationSession.get(session.id());
            else
                return "error " + e.getErrorCode() + " " + e.getMessage();
        } catch(URISyntaxException e) {
            return "error " + e.getMessage();
        }
        User user;
        try {
            //now, that we have the parameters, check whether the user is actually logged in
            String token = request.cookie("id_token");
            user = tokenService.validateIdToken(token);

        } catch (Exception e) {

            //if the user is not logged in, go to the login screen
            request.session(true);
            authenticationSession.put(request.session().id(), authenticationRequest);
            return render(new HashMap<>(), "login.mustache");
        }

        //create an access token and an in_token
        AccessToken accessToken = tokenService.grantAccess(user);
        IdToken idToken = tokenService.createIdToken(user);

        //delete session
        request.session().invalidate();

        //redirect with access token reponse
        String redirectUrl = authenticationRequest.getRedirectUri() + "#" +
                "access_token=" + accessToken.getToken() +
                "&token_type=bearer&" +
                "&id_token=" + idToken.getToken() +
                "&expires_id=not_set";

        if (!authenticationRequest.getState().isEmpty())
                redirectUrl += "&state=" + authenticationRequest.getState();
        response.redirect(redirectUrl, 302);
        return "";

    }


}
