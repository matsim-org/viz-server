package org.matsim.viz.error;

import javax.ws.rs.core.Response;

public class ForbiddenException extends CodedException {
    public ForbiddenException(String message) {
        super(Response.Status.FORBIDDEN.getStatusCode(), Error.FORBIDDEN, message);
    }
}
