package org.matsim.webvis.common.errorHandling;

public class ForbiddenException extends CodedException {
    public ForbiddenException(String message) {
        super(Error.FORBIDDEN, message);
    }
}
