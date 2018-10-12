package org.matsim.viz.auth.user;

import io.dropwizard.views.View;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.matsim.viz.auth.entities.Token;
import org.matsim.viz.auth.entities.User;
import org.matsim.viz.auth.token.TokenService;
import org.matsim.viz.error.UnauthorizedException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/login")
@Produces(MediaType.TEXT_HTML)
public class LoginResource {

    UserService userService = UserService.Instance;
    TokenService tokenService = TokenService.Instance;

    @POST
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password))
            return Response.status(Response.Status.OK).type(MediaType.TEXT_HTML)
                    .entity(LoginView.create()).build();

        User user;
        try {
            user = userService.authenticate(username, password.toCharArray());
        } catch (UnauthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.TEXT_HTML)
                    .entity(LoginView.createWithError()).build();
        }

        Token idToken = tokenService.createIdToken(user);

        //creates cookie which is always sent to any auth server route, never expires, is secure, is http only.
        NewCookie cookie = new NewCookie(
                "login", idToken.getTokenValue(), "/", "", "no-comment",
                -1, false, true
        );

        return Response.seeOther(URI.create("/authorize/from-login")).cookie(cookie).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public LoginView login() {
        return LoginView.create();
    }

    @Getter
    static class LoginView extends View {

        private final boolean error;
        private final String errorMessage = "username or password was wrong";

        LoginView(boolean withError) {
            super("/templates/login.mustache");
            this.error = withError;
        }

        static LoginView create() {
            return new LoginView(false);
        }

        static LoginView createWithError() {
            return new LoginView(true);
        }
    }
}
