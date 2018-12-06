package org.matsim.viz.auth.user;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path("/logout")
@Produces(MediaType.TEXT_HTML)
public class LogoutResource {

    @POST
    public Response logout() {
        // override the login cookie with a new cookie that immediately
        return Response
                .accepted()
                .cookie(new NewCookie("login", "", "/", "", "", 0, true, true))
                .build();
    }
}
