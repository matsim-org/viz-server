package org.matsim.webvis.error;

import javax.ws.rs.core.Response;

public class ForbiddenException extends CodedException {
    public ForbiddenException(String message) {
        super(Response.Status.FORBIDDEN.getStatusCode(), Error.FORBIDDEN, message);
    }
}
