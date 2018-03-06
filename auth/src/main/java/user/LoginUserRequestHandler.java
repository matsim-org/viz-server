package user;

import data.entities.IdToken;
import data.entities.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.mustache.MustacheTemplateEngine;
import token.TokenService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginUserRequestHandler implements Route {

    UserService userService = new UserService();
    TokenService tokenService;

    public LoginUserRequestHandler() throws UnsupportedEncodingException {
        tokenService = new TokenService();
    }

    public static String render(Map<String, Object> model, String path) {
        return new MustacheTemplateEngine().render(new ModelAndView(model, path));
    }

    @Override
    public Object handle(Request request, Response response) {

        request.session(true);

        //get username and password from request
        String username = request.queryParams("username");
        char[] password = request.queryParams("password").toCharArray();

        //if no params were sent show login
        if (username == null || password.length == 0) {
            return render(new HashMap<>(), "login.mustache");
        }

        //authenticate in user service
        User user;
        try {
            user = userService.authenticate(username, password);
        } catch (Exception e) {
            //if user was not authenticated display login page with error message
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("error", true);
            parameters.put("errorMessage", "username or password was wrong");
            return render(parameters, "login.mustache");
        }

        //create a token
        IdToken idToken = tokenService.createIdToken(user);

        //put token into a secure httpOnly cookie
        response.cookie("/", "id_token", idToken.getToken(), -1, false, true);

        //redirect to route which redirected to login
        response.redirect("/authorize/", 302);

        return "";
    }
}
