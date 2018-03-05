package user;

import data.entities.User;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginUserRequestHandler implements Route {

    UserService userService = new UserService();

    @Override
    public Object handle(Request request, Response response) throws Exception {

        request.session(true);

        //get username and password from request

        //authenticate in user service
        User user = userService.authenticate("username", new char[12]);

        //create a token
        //put token into a secure httpOnly cookie

        //redirect to route which redirected to login


        String callbackURi = request.session().attribute("callback");
        response.cookie("/", "some", "loginvalue", -1, false);
        response.redirect("/authorize/", 302);
        return "";
    }
}
