package org.matsim.webvis.common.errorHandling;

public class UnauthorizedException extends CodedException {

    public UnauthorizedException(String message) {
        super(Error.UNAUTHORIZED, message);
    }
}
