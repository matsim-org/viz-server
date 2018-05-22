package org.matsim.webvis.common.service;

import org.matsim.webvis.common.communication.RequestError;

public class UnauthorizedException extends CodedException {

    public UnauthorizedException(String message) {
        super(Error.UNAUTHORIZED, message);
    }
}
