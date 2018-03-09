package user;

import data.entities.IdToken;
import data.entities.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.mustache.MustacheTemplateEngine;
import token.TokenService;

import java.util.Map;

public class LoginUserRequestHandler implements Route {

    private UserService userService = new UserService();
    private TokenService tokenService;

    public LoginUserRequestHandler() {
        tokenService = new TokenService();
    }

    public static String render(Map<String, Object> model, String path) {
        return new MustacheTemplateEngine().render(new ModelAndView(model, path));
    }

    @Override
    public Object handle(Request request, Response response) {

        String username = request.queryParams("username");
        char[] password = request.queryParams("password").toCharArray();

        //if no params were sent show login
        if (username == null || password.length == 0) {
            return LoginPrompt.renderLogin();
        }

        User user;
        try {
            user = userService.authenticate(username, password);
        } catch (Exception e) {
            //if user was not authenticated display login page with error message
            return LoginPrompt.renderLoginWithError();
        }

        IdToken idToken = tokenService.createIdToken(user);

        //put token into a httpOnly cookie
        //TODO: make it a secure cookie when TLS is implemented
        response.cookie("/", "id_token", idToken.getToken(), -1, false, true);

        //redirect to route which redirected to login for now it's always authorize
        response.redirect("/authorize/", 302);

        return "OK";
    }
}
