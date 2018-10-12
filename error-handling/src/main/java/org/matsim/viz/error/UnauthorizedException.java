package org.matsim.viz.error;

import javax.ws.rs.core.Response;

public class UnauthorizedException extends CodedException {

    public UnauthorizedException(String message) {
        super(Response.Status.UNAUTHORIZED.getStatusCode(), Error.UNAUTHORIZED, message);
    }
}
