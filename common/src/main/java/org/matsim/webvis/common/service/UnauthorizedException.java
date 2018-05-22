package org.matsim.webvis.common.service;

public class UnauthorizedException extends CodedException {

    public UnauthorizedException(String message) {
        super(Error.UNAUTHORIZED, message);
    }
}
