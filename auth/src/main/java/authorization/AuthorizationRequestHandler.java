package authorization;

import data.entities.AccessToken;
import data.entities.User;
import spark.*;
import spark.template.mustache.MustacheTemplateEngine;
import token.TokenService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthorizationRequestHandler implements Route {

    private static Map<String, ImplicitAuthorizationRequest> loginSession = new ConcurrentHashMap<>();
    private TokenService tokenService = new TokenService();

    public AuthorizationRequestHandler() throws UnsupportedEncodingException {
    }

    private static String render(Map<String, Object> model, String path) {
        return new MustacheTemplateEngine().render(new ModelAndView(model, path));
    }

    @Override
    public Object handle(Request request, Response response) {

        ImplicitAuthorizationRequest authRequest;

        try {

            //try to form a request from request parameters
            authRequest = ImplicitAuthorizationRequest.create(request);
        } catch (Exception e) {

            //if this is not possible get the parameters from the session if one is available
            //likely the user agent got redireced from the login handler
            Session session = request.session();

            if (session != null && loginSession.containsKey(session.id())) {
                authRequest = loginSession.get(session.id());
            } else {
                return "error"; //this should be a proper error response
            }
        }

        try {
            //now, that we have the parameters, check whether the user is actually logged in
            String token = request.cookie("id_token");
            User user = tokenService.validateIdToken(token);

            //create an access token and an in_token
            AccessToken accessToken = tokenService.grantAccess(user);

            //delete session
            request.session().invalidate();

            //redirect with access token reponse
            String redirectUrl = authRequest.getRedirectURI() + "#" + "access_token=" + accessToken.getToken() +
                    "&token_type=bearer&state=" + authRequest.getState();
            response.redirect(redirectUrl, 302);

        } catch (Exception e) {

            //if the user is not logged in, go to the login screen
            request.session(true);
            loginSession.put(request.session().id(), authRequest);
            return render(new HashMap<>(), "login.mustache");
        }
        return "";
    }
}
