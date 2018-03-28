package org.matsim.webvis.files.communication;

import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.user.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AuthenticatedRequestHandler implements Route {

    private UserService userService = new UserService();

    @Override
    public Object handle(Request request, Response response) {

        IntrospectionResponse introspection = request.attribute(AuthenticationHandler.SUBJECT_ATTRIBUTE);
        User user = userService.findByIdentityProviderId(introspection.getSub());

        if (user == null) {

            //since the user presented a valid token we can assume he is registered with the auth server
            user = userService.createUser(introspection.getSub());
        }
        return handle(request, response, user);
    }

    protected abstract Object handle(Request request, Response response, User subject);
}
