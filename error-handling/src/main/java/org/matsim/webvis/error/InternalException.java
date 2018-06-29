package org.matsim.webvis.error;

import javax.ws.rs.core.Response;

public class InternalException extends CodedException {

    public InternalException(String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Error.UNSPECIFIED_ERROR, message);
    }
}
