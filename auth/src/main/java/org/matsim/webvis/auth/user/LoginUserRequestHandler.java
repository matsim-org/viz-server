package org.matsim.webvis.auth.user;

import org.matsim.webvis.auth.entities.IdToken;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.token.TokenService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginUserRequestHandler implements Route {

    UserService userService = new UserService();
    TokenService tokenService = new TokenService();

    public LoginUserRequestHandler() throws Exception {
    }

    @Override
    public Object handle(Request request, Response response) {

        String username = request.queryParams("username");
        String password = request.queryParams("password");

        //if no params were sent show login
        if (username == null || password == null) {
            return LoginPrompt.renderLogin();
        }

        User user;
        try {
            user = userService.authenticate(username, password.toCharArray());
        } catch (Exception e) {
            //if org.matsim.webvis.auth.user was not authenticated display login page with error message
            return LoginPrompt.renderLoginWithError();
        }

        IdToken idToken = tokenService.createIdToken(user);

        //put org.matsim.webvis.auth.token into a httpOnly cookie
        //TODO: make it a secure cookie when TLS is implemented
        response.cookie("/", "id_token", idToken.getToken(), -1, false, true);

        //redirect to route which redirected to login for now it's always authorize
        response.redirect("/authorize/", 302);

        return "OK";
    }
}
