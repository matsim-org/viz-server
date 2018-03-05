package authorization;

import spark.Redirect;
import spark.Request;
import spark.Response;
import spark.Route;

public class AuthorizationRequestHandler implements Route {


    @Override
    public Object handle(Request request, Response response) {

        try {
            ImplicitAuthorizationRequest authRequest = ImplicitAuthorizationRequest.create(request);
        } catch (Exception e) {

            return "error";
        }

        String token = request.cookie("id_token");
        if (validateToken(token)) {

            //get the parameters from session or from request parameters
            //create an access token and an in_token

            //delete session and cookies which are not id_token cookie
            // redirect to callback url with access token

        } else {
            //set up a session and redirect to login
            request.session(true);
            request.session().attribute("callback", "http://calback.de");
            response.redirect("/login.html", Redirect.Status.FOUND.intValue());
        }


        return null;
    }

    private boolean validateToken(String token) {
        return false;
    }

    private void processRequest(Request request) {

    }
}
