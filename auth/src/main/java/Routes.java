import authorization.AuthorizationRequestHandler;
import token.TokenRequestHandler;
import user.CreateUserRequestHandler;
import user.LoginUserRequestHandler;

import java.io.UnsupportedEncodingException;

import static spark.Spark.*;

public class Routes {

    private final static String USER = "user/";
    private final static String TOKEN = "token/";
    private final static String AUTHORIZE = "authorize/";
    private final static String LOGIN = "login/";

    static void initialize() throws UnsupportedEncodingException {

        put(USER, new CreateUserRequestHandler());
        post(TOKEN, new TokenRequestHandler());
        get(AUTHORIZE, new AuthorizationRequestHandler());
        post(LOGIN, new LoginUserRequestHandler());

        post("", (req, res) -> "{ error: 'not found', request: " + req.url() + " }");
    }
}
