import static spark.Spark.post;

public class Routes {

    private final static String TOKEN = "token/";

    static void initialize() {
        post(TOKEN, new TokenRequestHandler());
    }
}
