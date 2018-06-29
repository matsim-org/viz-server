package org.matsim.webvis.error;

import javax.ws.rs.core.Response;

public class InvalidInputException extends CodedException {
    public InvalidInputException(String message) {
        super(Response.Status.BAD_REQUEST.getStatusCode(), Error.INVALID_REQUEST, message);
    }
}
