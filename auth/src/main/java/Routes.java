import user.CreateUserRequestHandler;

import static spark.Spark.post;
import static spark.Spark.put;

public class Routes {

    private final static String USER = "user/";
    private final static String TOKEN = "token/";

    static void initialize() {

        put(USER, new CreateUserRequestHandler());
        post(TOKEN, new TokenRequestHandler());
    }
}
