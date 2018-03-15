import authorization.AuthorizationRequestHandler;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import token.IntrospectionRequestHandler;
import token.TokenRequestHandler;
import user.CreateUserRequestHandler;
import user.LoginUserRequestHandler;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Routes {

    private final static String USER = "user/";
    private final static String TOKEN = "token/";
    private final static String INTROSPECT = "introspect/";
    private final static String AUTHORIZE = "authorize/";
    private final static String LOGIN = "login/";
    private final static String LOGIN_FORM = "login/form/";

    static void initialize() throws Exception {

        // this allows cross origin requests for all sites for all http-methods
        options("/*", (request, response) -> {

            response.header("Access-Control-Allow-Headers", "Content-Type");
            response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

            return "OK";
        });

        before("/*", (request, response) -> {

            String origin = request.headers("Origin");
            response.header("Access-Control-Allow-Origin", (origin != null) ? origin : "*");
            response.header("Access-Control-Allow-Headers", "Content-Type");
            response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        });

        put(USER, new CreateUserRequestHandler());
        post(TOKEN, new TokenRequestHandler());
        post(INTROSPECT, new IntrospectionRequestHandler());
        post(AUTHORIZE, new AuthorizationRequestHandler());
        get(AUTHORIZE, new AuthorizationRequestHandler());
        get(LOGIN_FORM, (request, response) -> render(new HashMap<>(), "login.mustache"));
        post(LOGIN, new LoginUserRequestHandler());

        post("/", (req, res) -> "{ error: 'not found', request: " + req.url() + " }");
    }

    private static String render(Map<String, Object> model, String path) {
        return new MustacheTemplateEngine().render(new ModelAndView(model, path));
    }
}
